import subprocess
import sys
import textwrap

try:
    output = subprocess.check_output([
        'tooling/pmd-bin-6.7.0/bin/run.sh',
        'pmd',
        '-d',
        'eevee/src',
        '-f',
        'text',
        '-R',
        'category/java/errorprone.xml,category/java/performance.xml'
    ], stderr=subprocess.STDOUT)

    print('Wow you\'re like amazing probably. No PMD warnings!')
except subprocess.CalledProcessError as e:
    output = e.output.decode('utf-8').split('\n')[2:]

    ur_anomolies = 0
    du_anomolies = 0
    dd_anomolies = 0

    actual_problems = []

    for line in output:
        if "Found 'UR'-anomaly" in line:
            ur_anomolies += 1
        elif "Found 'DU'-anomaly" in line:
            du_anomolies += 1
        elif "Found 'DD'-anomaly" in line:
            dd_anomolies += 1
        elif "Found non-transient, non-static member. " in line:
            pass
        elif "It is somewhat confusing to have a field name ma" in line:
            pass
        elif "Avoid using Literals in Conditional Statements" in line:
            pass
        else:
            actual_problems.append(line)

    print(textwrap.dedent(
        """
        We trust that you know what you're doing but just
        in case here are your PMD results (and maybe some warnings).
        This test has been marked as a failure in Bazel but may
        be ignored if you think it's okay.
        
        //---------------------------------------------
        // Maybe Important PMD Results:
        //---------------------------------------------
        
        Reference Statistics You Probably Don't Care About:
        Possible Undefined References: {}
        Recently Undefined References: {}
        Recently Redefined References: {}
        
        //---------------------------------------------
        // Stuff That You May Actually Care About:
        //---------------------------------------------
        {}""").format(
        ur_anomolies, du_anomolies, dd_anomolies, "\n".join(actual_problems)
    ))
    sys.exit(1)