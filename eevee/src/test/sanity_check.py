import os
import re
import subprocess
import sys

'''
Disable sanity checks by adding the following flags
to the beginning of your file.
'''
IGNORE_ENFORCED_SAFETY_FLAG = '// SANITY_IGNORE_ENFORCED_SAFETY'
IGNORE_EXTRA_INSTANTIATIONS = '// SANITY_IGNORE_EXTRA_INSTANTIATIONS'

max_instantiations = 3

found_errors = False


def err(*args, **kwargs):
    print(*args, file=sys.stderr, **kwargs)
    global found_errors
    found_errors = True


def check_enforced_safety(f, lines):
    for i in range(len(lines)):
        line = lines[i]
        m = re.search(r'\.queue\(', line)

        if m is not None:
            err(
                'Found usage of regular JDA queue method. Please use EnforcedSafetyAction.\n' +
                '    File: {}\n    Line: {}\n    {}\n'.format(f, i, line)
            )


def check_instantiations(f, lines):
    base_type_creations = {}
    var_name_usages = {}
    reassigned_vars = {}

    for line in lines:
        # TODO: This should not aggregate instantiations in different methods.
        m = re.search(r'(.+) (\w+) = new (.+)\(', line)
        reassignments = re.search(r'^(\w+) =', line)

        if m is not None:
            base_type = m.group(1)
            var_name = m.group(2)

            if base_type not in base_type_creations:
                base_type_creations[base_type] = 0

            if var_name not in var_name_usages:
                var_name_usages[var_name] = 0

            base_type_creations[base_type] += 1
            var_name_usages[var_name] += 1

        if reassignments is not None:
            reassigned_var = reassignments.group(1)

            if reassigned_var not in reassigned_vars:
                reassigned_vars[reassigned_var] = 0
            reassigned_vars[reassigned_var] += 1


    for (k, v) in base_type_creations.items():
        if v > max_instantiations:
            err(
                'Variable type "{}" is instantiated more than {} times. '.format(
                    k, max_instantiations
                ) + 'Consider abstracting to a helper method.\n    File: {}'.format(f)
            )

    for (k, v) in reassigned_vars.items():
        if v > max_instantiations:
            err(
                'Variable "{}" is reassigned more than {} times. '.format(
                    k, max_instantiations
                ) + 'Consider creating unique names for each usage.\n    File: {}'.format(f)
            )


def process_file(f):
    with open(f, encoding='utf-8') as file_desc:
        lines = [x.strip() for x in file_desc.readlines()]

        if IGNORE_ENFORCED_SAFETY_FLAG not in lines:
            check_enforced_safety(f, lines)
        if IGNORE_EXTRA_INSTANTIATIONS not in lines:
            check_instantiations(f, lines)


def process_dir(d):
    for f in os.listdir(d):
        if f in ['.', '..']:
            continue
        full_path = os.path.join(d, f)
        if os.path.isdir(full_path):
            process_dir(full_path)
        if f.endswith('.java'):
            process_file(full_path)


process_dir('eevee/src/main/java/')

if os.path.isfile('eevee/conf/Eevee.json'):
    try:
        output = subprocess.check_output(['git', 'ls-files', 'conf/Eevee.json'])
        if output:
            err('Found Coffee canary file in source control. Builds will be blocked until this file is removed.')
    except:
            pass

if found_errors:
    sys.exit(1)