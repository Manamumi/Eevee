import os
import re
import subprocess
import sys
import textwrap

if len(sys.argv) != 3:
    print(
        textwrap.dedent(
            '''
            keyz is a utility for injecting secrets and other
            metadata into nomad job files at runtime for automated
            deployment.
            
            Usage:
            keyz.par path/to/nomad/configs path/to/secret/files
            
            Make sure you have blackbox_cat on your path and your
            GPG key is authorized to use it.
            '''
        )
    )
    sys.exit(0)

nomad_conf_dir = sys.argv[1]
secrets_dir = sys.argv[2]

inject_regex = re.compile(r'(secret|env):([A-Za-z_\-]+)')


def inject(match):
    inject_type = match.group(1)
    key = match.group(2)

    if inject_type == 'secret':
        return subprocess.check_output(['blackbox_cat', os.path.join(secrets_dir, key)]).strip()
    elif inject_type == 'env':
        return os.environ[key]


for f in os.listdir(nomad_conf_dir):
    if not f.endswith('.nomad'):
        continue

    with open(os.path.join(nomad_conf_dir, f), 'r') as nomad_file:
        contents = nomad_file.readlines()
        contents = [
            inject_regex.sub(
                inject, line
            )
            for line in contents
        ]

    with open(os.path.join(nomad_conf_dir, f), 'w') as nomad_file:
        nomad_file.writelines(contents)