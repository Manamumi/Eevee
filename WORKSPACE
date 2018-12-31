workspace(name = "eevee")

# TODO: Change this back!!!
# We are using a modified version of rules_python that forces Python3.
#local_repository(
#    name = "io_bazel_rules_python",
#    path = "third-party/rules_python",
#)

git_repository(
    name = "io_bazel_rules_python",
    # NOT VALID!  Replace this with a Git commit SHA.
    commit = "8b5d0683a7d878b28fffe464779c8a53659fc645",
    remote = "https://github.com/bazelbuild/rules_python.git",
)

# Only needed for PIP support:
load("@io_bazel_rules_python//python:pip.bzl", "pip_repositories")

pip_repositories()

load("@io_bazel_rules_python//python:pip.bzl", "pip_import")

# This rule translates the specified requirements.txt into
# @my_deps//:requirements.bzl, which itself exposes a pip_install method.
pip_import(
    name = "inside_deps",
    requirements = "//inside:requirements.txt",
)

# Load the pip_install symbol for my_deps, and create the dependencies'
# repositories.
load("@inside_deps//:requirements.bzl", "pip_install")

pip_install()

git_repository(
    name = "subpar",
    remote = "https://github.com/google/subpar",
    tag = "1.0.0",
)

git_repository(
    name = "org_pubref_rules_maven",
    commit = "9c3b07a",
    remote = "https://github.com/pubref/rules_maven",
)

load("@org_pubref_rules_maven//maven:rules.bzl", "maven_repositories", "maven_repository")

maven_repositories()

maven_repository(
    name = "logging_maven",
    transitive_deps = [
        "268f0fe4df3eefe052b57c87ec48517d64fb2a10:org.apache.logging.log4j:log4j-api:2.11.1",
        "592a48674c926b01a9a747c7831bcd82a9e6d6e4:org.apache.logging.log4j:log4j-core:2.11.1",
        "9e0d2cb33b3416eee21ef1f930ee595d8705541c:org.apache.logging.log4j:log4j-slf4j18-impl:2.11.1",
        "34fa1d87256bbdb376ae7f6fa7e479610cd07dce:org.slf4j:slf4j-api:1.8.0-alpha2",
    ],
    deps = [
        "org.apache.logging.log4j:log4j-core:2.11.1",
        "org.apache.logging.log4j:log4j-slf4j18-impl:2.11.1",
    ],
)

load("@logging_maven//:rules.bzl", "logging_maven_compile")

logging_maven_compile()

maven_repository(
    name = "jackson_maven",
    transitive_deps = [
        "07c10d545325e3a6e72e06381afe469fd40eb701:com.fasterxml.jackson.core:jackson-annotations:2.9.0",
        "a22ac51016944b06fd9ffbc9541c6e7ce5eea117:com.fasterxml.jackson.core:jackson-core:2.9.5",
        "3490508379d065fe3fcb80042b62f630f7588606:com.fasterxml.jackson.core:jackson-databind:2.9.5",
        "023e37f085279ba316c0df923513b81609e1d1f6:com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.9.5",
        "d1f0d11e816bc04e222a261106ca138801841c2d:com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.5",
    ],
    deps = [
        "com.fasterxml.jackson.core:jackson-databind:2.9.5",
        "com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.9.5",
        "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.5",
    ],
)

load("@jackson_maven//:rules.bzl", "jackson_maven_compile")

jackson_maven_compile()

maven_repository(
    name = "javelin_maven",
    force = [
        "org.jetbrains:annotations:16.0.1",
        "org.slf4j:slf4j-api:1.8.0-alpha2",
    ],
    transitive_deps = [
        "2d13ba8f4e9e578ef3636c064dae4ed675c8de7d:com.github.spullara.mustache.java:compiler:0.9.5",
        "efe65d0ba9a25346c85fdbcf2560184bb698fdf3:io.javalin:javalin:1.7.0",
        "3cd63d075497751784b2fa84be59432f4905bf7c:javax.servlet:javax.servlet-api:3.1.0",
        "c6ed57d55c5d55927c6e11fc47972a4eeacba6f4:org.eclipse.jetty:jetty-client:9.4.9.v20180320",
        "64d93698196ea7a66b33c754a0eac2a97d5af4b6:org.eclipse.jetty:jetty-http:9.4.9.v20180320",
        "938d67c72405285d2a7a6efb10d870a1b16fa2e0:org.eclipse.jetty:jetty-io:9.4.9.v20180320",
        "dadd28ef757d9b8cdd1d7eef7fcbfa0b482c4648:org.eclipse.jetty:jetty-security:9.4.9.v20180320",
        "08847f7278e8ace7a1f5847e71563c8a10546582:org.eclipse.jetty:jetty-server:9.4.9.v20180320",
        "d4453b746bc581af6ec5bce09228dc802bec1040:org.eclipse.jetty:jetty-servlet:9.4.9.v20180320",
        "8a602b93581f6af54839728f51d51ab830bdd44d:org.eclipse.jetty:jetty-util:9.4.9.v20180320",
        "e10113bbae52fa213ede3cafb9235ba1b1884bed:org.eclipse.jetty:jetty-webapp:9.4.9.v20180320",
        "247894128d4d3ba41774be941fb801e20e668be7:org.eclipse.jetty:jetty-xml:9.4.9.v20180320",
        "8be09aafe135e370bbc33a8ec01b443ac9aa9a47:org.eclipse.jetty.websocket:websocket-api:9.4.9.v20180320",
        "7bd061a1e4f061a02c9fa1441ff93aca213b303c:org.eclipse.jetty.websocket:websocket-client:9.4.9.v20180320",
        "ca1f96a14ae58d951d1d93c1c5a3416b4b501578:org.eclipse.jetty.websocket:websocket-common:9.4.9.v20180320",
        "c278d6649a5c754f5ae5182f4ee680309c3880d3:org.eclipse.jetty.websocket:websocket-server:9.4.9.v20180320",
        "1e443fa4b3ac4c8cee483164479ff3d01b5fcf21:org.eclipse.jetty.websocket:websocket-servlet:9.4.9.v20180320",
        "c1a6655cebcac68e63e4c24d23f573035032eb2a:org.jetbrains:annotations:16.0.1",
        "7e34f009642702250bccd9e5255866f408962a05:org.jetbrains.kotlin:kotlin-stdlib:1.2.41",
        "d0cfb3ef897c00449e5e696355db9506225fb507:org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.2.41",
        "5e34ca185bbea7452d704ed3537a22314a809383:org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.2.41",
        "34fa1d87256bbdb376ae7f6fa7e479610cd07dce:org.slf4j:slf4j-api:1.8.0-alpha2",
    ],
    deps = [
        "com.github.spullara.mustache.java:compiler:0.9.5",
        "io.javalin:javalin:1.7.0",
    ],
)

load("@javelin_maven//:rules.bzl", "javelin_maven_compile")

javelin_maven_compile()

maven_repository(
    name = "http_client_maven",
    exclude = {
        "com.google.http-client:google-http-client-jackson2": [
            "com.fasterxml.jackson.core:jackson-core",
        ],
    },
    force = [
        "com.google.code.findbugs:jsr305:3.0.2",
    ],
    transitive_deps = [
        "25ea2e8b0c338a877313bd4672d3fe056ea78f0d:com.google.code.findbugs:jsr305:3.0.2",
        "8e86c84ff3c98eca6423e97780325b299133d858:com.google.http-client:google-http-client:1.23.0",
        "fd6761f4046a8cb0455e6fa5f58e12b061e9826e:com.google.http-client:google-http-client-jackson2:1.23.0",
        "fd32786786e2adb664d5ecc965da47629dca14ba:commons-codec:commons-codec:1.3",
        "5043bfebc3db072ed80fbd362e7caf00e885d8ae:commons-logging:commons-logging:1.1.1",
        "1d7d28fa738bdbfe4fbd895d9486308999bdf440:org.apache.httpcomponents:httpclient:4.0.1",
        "e813b8722c387b22e1adccf7914729db09bcb4a9:org.apache.httpcomponents:httpcore:4.0.1",
    ],
    deps = [
        "com.google.http-client:google-http-client-jackson2:1.23.0",
    ],
)

load("@http_client_maven//:rules.bzl", "http_client_maven_compile")

http_client_maven_compile()

maven_repository(
    name = "google_guava_maven",
    transitive_deps = [
        "25ea2e8b0c338a877313bd4672d3fe056ea78f0d:com.google.code.findbugs:jsr305:3.0.2",
        "39b109f2cd352b2d71b52a3b5a1a9850e1dc304b:com.google.errorprone:error_prone_annotations:2.1.3",
        "6a806eff209f36f635f943e16d97491f00f6bfab:com.google.guava:guava:26.0-jre",
        "ed28ded51a8b1c6b112568def5f4b455e6809019:com.google.j2objc:j2objc-annotations:1.1",
        "cea74543d5904a30861a61b4643a5f2bb372efc4:org.checkerframework:checker-qual:2.5.2",
        "775b7e22fb10026eed3f86e8dc556dfafe35f2d5:org.codehaus.mojo:animal-sniffer-annotations:1.14",
    ],
    deps = [
        "com.google.guava:guava:26.0-jre",
    ],
)

load("@google_guava_maven//:rules.bzl", "google_guava_maven_compile")

google_guava_maven_compile()

maven_repository(
    name = "google_cloud_translate_maven",
    force = [
        "com.google.code.findbugs:jsr305:3.0.2",
        "com.google.guava:guava:26.0-jre",
        "com.google.http-client:google-http-client:1.23.0",
        "com.google.http-client:google-http-client-jackson2:1.23.0",
        "com.fasterxml.jackson.core:jackson-core:2.9.5",
        "com.google.api.grpc:proto-google-common-protos:1.10.0",
        "com.google.errorprone:error_prone_annotations:2.1.3",
    ],
    transitive_deps = [
        "a22ac51016944b06fd9ffbc9541c6e7ce5eea117:com.fasterxml.jackson.core:jackson-core:2.9.5",
        "7e537338d40a57ad469239acb6d828fa544fb52b:com.google.api:api-common:1.5.0",
        "522ea860eb48dee71dfe2c61a1fd09663539f556:com.google.api-client:google-api-client:1.23.0",
        "36ab73c0b5d4a67447eb89a3174cc76ced150bd1:com.google.api:gax:1.25.0",
        "e11fd47991c72f20b81958c93ce60340f29c90aa:com.google.api:gax-httpjson:0.42.0",
        "88563e83d9e7d6c6b1d92d85b6579da37b9efdd5:com.google.api.grpc:proto-google-common-protos:1.10.0",
        "5f01e99c6c1cd651c674a075a019e43646ab9c98:com.google.api.grpc:proto-google-iam-v1:0.11.0",
        "884c99a70ead22855a673509a21f5d5f5246812c:com.google.apis:google-api-services-translate:v2-rev47-1.22.0",
        "25e0f45f3b3d1b4fccc8944845e51a7a4f359652:com.google.auth:google-auth-library-credentials:0.9.1",
        "c0fe3a39b0f28d59de1986b3c50f018cd7cb9ec2:com.google.auth:google-auth-library-oauth2-http:0.9.1",
        "c0e88c78ce17c92d76bf46345faf3fa68833b216:com.google.cloud:google-cloud-core:1.28.0",
        "7b4559a9513abd98da50958c56a10f8ae00cb0f7:com.google.cloud:google-cloud-core-http:1.28.0",
        "2d8502d89de9ae93714e71644c597099629c3e3c:com.google.cloud:google-cloud-translate:1.28.0",
        "25ea2e8b0c338a877313bd4672d3fe056ea78f0d:com.google.code.findbugs:jsr305:3.0.2",
        "751f548c85fa49f330cecbb1875893f971b33c4e:com.google.code.gson:gson:2.7",
        "39b109f2cd352b2d71b52a3b5a1a9850e1dc304b:com.google.errorprone:error_prone_annotations:2.1.3",
        "6a806eff209f36f635f943e16d97491f00f6bfab:com.google.guava:guava:26.0-jre",
        "8e86c84ff3c98eca6423e97780325b299133d858:com.google.http-client:google-http-client:1.23.0",
        "0eda0d0f758c1cc525866e52e1226c4eb579d130:com.google.http-client:google-http-client-appengine:1.23.0",
        "a72ea3a197937ef63a893e73df312dac0d813663:com.google.http-client:google-http-client-jackson:1.23.0",
        "fd6761f4046a8cb0455e6fa5f58e12b061e9826e:com.google.http-client:google-http-client-jackson2:1.23.0",
        "ed28ded51a8b1c6b112568def5f4b455e6809019:com.google.j2objc:j2objc-annotations:1.1",
        "e57ea1e2220bda5a2bd24ff17860212861f3c5cf:com.google.oauth-client:google-oauth-client:1.23.0",
        "8c3492f7662fa1cbf8ca76a0f5eb1146f7725acd:com.google.protobuf:protobuf-java:3.5.1",
        "6e40a6a3f52455bd633aa2a0dba1a416e62b4575:com.google.protobuf:protobuf-java-util:3.5.1",
        "fd32786786e2adb664d5ecc965da47629dca14ba:commons-codec:commons-codec:1.3",
        "5043bfebc3db072ed80fbd362e7caf00e885d8ae:commons-logging:commons-logging:1.1.1",
        "28b0836f48c9705abf73829bbc536dba29a1329a:io.grpc:grpc-context:1.9.0",
        "54689fbf750a7f26e34fa1f1f96b883c53f51486:io.opencensus:opencensus-api:0.11.1",
        "82e572b41e81ecf58d0d1e9a3953a05aa8f9c84b:io.opencensus:opencensus-contrib-http-util:0.11.1",
        "36d6e77a419cb455e6fd5909f6f96b168e21e9d0:joda-time:joda-time:2.9.2",
        "1d7d28fa738bdbfe4fbd895d9486308999bdf440:org.apache.httpcomponents:httpclient:4.0.1",
        "e813b8722c387b22e1adccf7914729db09bcb4a9:org.apache.httpcomponents:httpcore:4.0.1",
        "cea74543d5904a30861a61b4643a5f2bb372efc4:org.checkerframework:checker-qual:2.5.2",
        "e32303ef8bd18a5c9272780d49b81c95e05ddf43:org.codehaus.jackson:jackson-core-asl:1.9.11",
        "775b7e22fb10026eed3f86e8dc556dfafe35f2d5:org.codehaus.mojo:animal-sniffer-annotations:1.14",
        "3ea31c96676ff12ab56be0b1af6fff61d1a4f1f2:org.threeten:threetenbp:1.3.3",
    ],
    deps = [
        "com.google.cloud:google-cloud-translate:1.28.0",
    ],
)

load("@google_cloud_translate_maven//:rules.bzl", "google_cloud_translate_maven_compile")

google_cloud_translate_maven_compile()

maven_repository(
    name = "jda_maven",
    exclude = {
        "net.dv8tion:JDA": [
            "club.minnced:opus-java",
        ],
    },
    force = [
        "org.slf4j:slf4j-api:1.8.0-alpha2",
    ],
    repositories = {
        "https://jcenter.bintray.com": [
            "net.dv8tion:JDA",
            "club.minnced:opus-java-api",
            "club.minnced:opus-java-natives",
        ],
    },
    transitive_deps = [
        "20e4fafa8523ed391446ddc7dff60ef832f1543a:club.minnced:opus-java-api:1.0.4",
        "292d015243833578eda04b1ad0af2dc351b14b1b:club.minnced:opus-java-natives:1.0.4",
        "25ea2e8b0c338a877313bd4672d3fe056ea78f0d:com.google.code.findbugs:jsr305:3.0.2",
        "562c5e33fbb23a576d5ae9b359dd554ce79de37e:com.neovisionaries:nv-websocket-client:2.5",
        "4d060ca3190df0eda4dc13415532a12e15ca5f11:com.squareup.okhttp3:okhttp:3.8.1",
        "a9283170b7305c8d92d25aff02a6ab7e45d06cbe:com.squareup.okio:okio:1.13.0",
        "e4c584fe0e9fba7188c8fdedb78bdce4d458a735:net.dv8tion:JDA:3.7.1_422",
        "cb208278274bf12ebdb56c61bd7407e6f774d65a:net.java.dev.jna:jna:4.4.0",
        "42ccaf4761f0dfdfa805c9e340d99a755907e2dd:net.sf.trove4j:trove4j:3.0.3",
        "a4cf4688fe1c7e3a63aa636cc96d013af537768e:org.apache.commons:commons-collections4:4.1",
        "c1a6655cebcac68e63e4c24d23f573035032eb2a:org.jetbrains:annotations:16.0.1",
        "aca5eb39e2a12fddd6c472b240afe9ebea3a6733:org.json:json:20160810",
        "34fa1d87256bbdb376ae7f6fa7e479610cd07dce:org.slf4j:slf4j-api:1.8.0-alpha2",
    ],
    deps = [
        "club.minnced:opus-java-api:1.0.4",
        "club.minnced:opus-java-natives:1.0.4",
        "net.dv8tion:JDA:3.7.1_422",
    ],
)

load("@jda_maven//:rules.bzl", "jda_maven_compile")

jda_maven_compile()

maven_repository(
    name = "mongo_maven",
    transitive_deps = [
        "e0768dd6b819e327e7e5623b9c82f39a5e7a66ed:org.mongodb:mongo-java-driver:3.7.0",
    ],
    deps = [
        "org.mongodb:mongo-java-driver:3.7.0",
    ],
)

load("@mongo_maven//:rules.bzl", "mongo_maven_compile")

mongo_maven_compile()

maven_repository(
    name = "twitter4j_maven",
    transitive_deps = [
        "f3722af4568b96ee66739267e13211b8b66ac7d4:org.twitter4j:twitter4j-core:4.0.6",
    ],
    deps = [
        "org.twitter4j:twitter4j-core:4.0.6",
    ],
)

load("@twitter4j_maven//:rules.bzl", "twitter4j_maven_compile")

twitter4j_maven_compile()

maven_repository(
    name = "amqp_maven",
    force = [
        "org.slf4j:slf4j-api:1.8.0-alpha2",
    ],
    transitive_deps = [
        "15c76125a515a1e0355ab974bd2d96b6d96b301e:com.rabbitmq:amqp-client:5.4.2",
        "34fa1d87256bbdb376ae7f6fa7e479610cd07dce:org.slf4j:slf4j-api:1.8.0-alpha2",
    ],
    deps = [
        "com.rabbitmq:amqp-client:5.4.2",
    ],
)

load("@amqp_maven//:rules.bzl", "amqp_maven_compile")

amqp_maven_compile()

maven_repository(
    name = "bt_maven",
    force = [
        "com.google.guava:guava:26.0-jre",
        "org.slf4j:slf4j-api:1.8.0-alpha2",
    ],
    transitive_deps = [
        "0235ba8b489512805ac13a8f9ea77a1ca5ebe3e8:aopalliance:aopalliance:1.0",
        "de4cd81636d5cfd1a7468b7580e4734f0cab97b6:com.github.atomashpolskiy:bt-bencoding:1.7",
        "eae8de585db804f438e814d53e1b31091a65b135:com.github.atomashpolskiy:bt-core:1.7",
        "25ea2e8b0c338a877313bd4672d3fe056ea78f0d:com.google.code.findbugs:jsr305:3.0.2",
        "39b109f2cd352b2d71b52a3b5a1a9850e1dc304b:com.google.errorprone:error_prone_annotations:2.1.3",
        "6a806eff209f36f635f943e16d97491f00f6bfab:com.google.guava:guava:26.0-jre",
        "3b27257997ac51b0f8d19676f1ea170427e86d51:com.google.inject.extensions:guice-multibindings:4.1.0",
        "eeb69005da379a10071aa4948c48d89250febb07:com.google.inject:guice:4.1.0",
        "ed28ded51a8b1c6b112568def5f4b455e6809019:com.google.j2objc:j2objc-annotations:1.1",
        "6975da39a7040257bd51d21a231b76c915872d38:javax.inject:javax.inject:1",
        "cea74543d5904a30861a61b4643a5f2bb372efc4:org.checkerframework:checker-qual:2.5.2",
        "775b7e22fb10026eed3f86e8dc556dfafe35f2d5:org.codehaus.mojo:animal-sniffer-annotations:1.14",
        "34fa1d87256bbdb376ae7f6fa7e479610cd07dce:org.slf4j:slf4j-api:1.8.0-alpha2",
        "7a27ea250c5130b2922b86dea63cbb1cc10a660c:org.yaml:snakeyaml:1.17",
    ],
    deps = [
        "com.github.atomashpolskiy:bt-core:1.7",
    ],
)

load("@bt_maven//:rules.bzl", "bt_maven_compile")

bt_maven_compile()

maven_repository(
    name = "disruptor_maven",
    transitive_deps = [
        "be71143c8ba540e34ced6fabf74ba9aa238156b6:com.lmax:disruptor:3.3.4",
    ],
    deps = [
        "com.lmax:disruptor:3.3.4",
    ],
)

load("@disruptor_maven//:rules.bzl", "disruptor_maven_compile")

disruptor_maven_compile()

maven_repository(
    name = "grpc_maven",
    force = [
        "io.grpc:grpc-context:1.9.0",
        "com.google.guava:guava:26.0-jre",
        "com.google.code.findbugs:jsr305:3.0.2",
        "com.google.errorprone:error_prone_annotations:2.1.3",
        "io.opencensus:opencensus-api:0.11.1",
        "com.google.api.grpc:proto-google-common-protos:1.10.0",
        "com.squareup.okio:okio:1.13.0",
        "com.google.auth:google-auth-library-credentials:0.9.1",
    ],
    transitive_deps = [
        "88563e83d9e7d6c6b1d92d85b6579da37b9efdd5:com.google.api.grpc:proto-google-common-protos:1.10.0",
        "25e0f45f3b3d1b4fccc8944845e51a7a4f359652:com.google.auth:google-auth-library-credentials:0.9.1",
        "25ea2e8b0c338a877313bd4672d3fe056ea78f0d:com.google.code.findbugs:jsr305:3.0.2",
        "751f548c85fa49f330cecbb1875893f971b33c4e:com.google.code.gson:gson:2.7",
        "39b109f2cd352b2d71b52a3b5a1a9850e1dc304b:com.google.errorprone:error_prone_annotations:2.1.3",
        "6a806eff209f36f635f943e16d97491f00f6bfab:com.google.guava:guava:26.0-jre",
        "ed28ded51a8b1c6b112568def5f4b455e6809019:com.google.j2objc:j2objc-annotations:1.1",
        "357e60f95cebb87c72151e49ba1f570d899734f8:com.google.protobuf.nano:protobuf-javanano:3.0.0-alpha-5",
        "8c3492f7662fa1cbf8ca76a0f5eb1146f7725acd:com.google.protobuf:protobuf-java:3.5.1",
        "6e40a6a3f52455bd633aa2a0dba1a416e62b4575:com.google.protobuf:protobuf-java-util:3.5.1",
        "4de2b4ed3445c37ec1720a7d214712e845a24636:com.squareup.okhttp:okhttp:2.5.0",
        "a9283170b7305c8d92d25aff02a6ab7e45d06cbe:com.squareup.okio:okio:1.13.0",
        "efa94ac1ca079af730257d8ce24e8f120479eda3:io.grpc:grpc-all:1.10.0",
        "42fd0f1f757ef071acd20aaa6512272dc273068e:io.grpc:grpc-auth:1.10.0",
        "28b0836f48c9705abf73829bbc536dba29a1329a:io.grpc:grpc-context:1.9.0",
        "8976afebf2a6530574a71bc1260920ce910c2292:io.grpc:grpc-core:1.10.0",
        "a1056d69003c9b46d1c4aa4a9167c6e8a714d152:io.grpc:grpc-netty:1.10.0",
        "2c9e1b901a53ca3176f3380204dbfad1e48c0ad4:io.grpc:grpc-okhttp:1.10.0",
        "64098f046f227b47238bc747e3cee6c7fc087bb8:io.grpc:grpc-protobuf:1.10.0",
        "b8e40dd308dc370e64bd2c337bb2761a03299a7f:io.grpc:grpc-protobuf-lite:1.10.0",
        "3580b5f42c5fbf5cc7430ef3237feb7def4690de:io.grpc:grpc-protobuf-nano:1.10.0",
        "d022706796b0820d388f83571da160fb8d280ded:io.grpc:grpc-stub:1.10.0",
        "575e43e4567e00d2ee7e46493a23c60bae0e5f0a:io.grpc:grpc-testing:1.10.0",
        "fdd68fb3defd7059a7392b9395ee941ef9bacc25:io.netty:netty-buffer:4.1.17.Final",
        "1d00f56dc9e55203a4bde5aae3d0828fdeb818e7:io.netty:netty-codec:4.1.17.Final",
        "251d7edcb897122b9b23f24ff793cd0739056b9e:io.netty:netty-codec-http:4.1.17.Final",
        "f9844005869c6d9049f4b677228a89fee4c6eab3:io.netty:netty-codec-http2:4.1.17.Final",
        "a159bf1f3d5019e0d561c92fbbec8400967471fa:io.netty:netty-codec-socks:4.1.17.Final",
        "581c8ee239e4dc0976c2405d155f475538325098:io.netty:netty-common:4.1.17.Final",
        "18c40ffb61a1d1979eca024087070762fdc4664a:io.netty:netty-handler:4.1.17.Final",
        "9330ee60c4e48ca60aac89b7bc5ec2567e84f28e:io.netty:netty-handler-proxy:4.1.17.Final",
        "8f386c80821e200f542da282ae1d3cde5cad8368:io.netty:netty-resolver:4.1.17.Final",
        "9585776b0a8153182412b5d5366061ff486914c1:io.netty:netty-transport:4.1.17.Final",
        "54689fbf750a7f26e34fa1f1f96b883c53f51486:io.opencensus:opencensus-api:0.11.1",
        "d57b877f1a28a613452d45e35c7faae5af585258:io.opencensus:opencensus-contrib-grpc-metrics:0.11.0",
        "2973d150c0dc1fefe998f834810d68f278ea58ec:junit:junit:4.12",
        "cea74543d5904a30861a61b4643a5f2bb372efc4:org.checkerframework:checker-qual:2.5.2",
        "775b7e22fb10026eed3f86e8dc556dfafe35f2d5:org.codehaus.mojo:animal-sniffer-annotations:1.14",
        "42a25dc3219429f0e5d060061f71acb49bf010a0:org.hamcrest:hamcrest-core:1.3",
        "c3264abeea62c4d2f367e21484fbb40c7e256393:org.mockito:mockito-core:1.9.5",
        "9b473564e792c2bdf1449da1f0b1b5bff9805704:org.objenesis:objenesis:1.0",
    ],
    deps = [
        "io.grpc:grpc-all:1.10.0",
    ],
)

load("@grpc_maven//:rules.bzl", "grpc_maven_compile")

grpc_maven_compile()

maven_jar(
    name = "org_projectlombok_lombok",
    artifact = "org.projectlombok:lombok:1.18.2",
    sha1 = "524e0a697e9d62950b2f763d88d35cd8dc82a9a1",
)

git_repository(
    name = "org_pubref_rules_protobuf",
    remote = "https://github.com/pubref/rules_protobuf",
    tag = "v0.8.2",
)

load("@org_pubref_rules_protobuf//java:rules.bzl", "java_proto_repositories")

java_proto_repositories(
    overrides = {
        "com_google_api_grpc_proto_google_common_protos": {
            "rule": "maven_jar",
            "artifact": "com.google.api.grpc:proto-google-common-protos:1.10.0",
            "sha1": "88563e83d9e7d6c6b1d92d85b6579da37b9efdd5",
        },
        "com_google_code_findbugs_jsr305": {
            "rule": "maven_jar",
            "artifact": "com.google.code.findbugs:jsr305:3.0.2",
            "sha1": "25ea2e8b0c338a877313bd4672d3fe056ea78f0d",
        },
        "com_google_errorprone_error_prone_annotations": {
            "rule": "maven_jar",
            "artifact": "com.google.errorprone:error_prone_annotations:2.1.3",
            "sha1": "39b109f2cd352b2d71b52a3b5a1a9850e1dc304b",
        },
        "com_google_guava_guava": {
            "rule": "maven_jar",
            "artifact": "com.google.guava:guava:26.0-jre",
            "sha1": "6a806eff209f36f635f943e16d97491f00f6bfab",
        },
        "com_squareup_okio_okio": {
            "rule": "maven_jar",
            "artifact": "com.squareup.okio:okio:1.13.0",
            "sha1": "a9283170b7305c8d92d25aff02a6ab7e45d06cbe",
        },
        "io_opencensus_opencensus_api": {
            "rule": "maven_jar",
            "artifact": "io.opencensus:opencensus-api:0.11.1",
            "sha1": "54689fbf750a7f26e34fa1f1f96b883c53f51486",
        },
        "io_grpc_grpc_core": {
            "rule": "maven_jar",
            "artifact": "io.grpc:grpc-core:1.10.0",
            "sha1": "8976afebf2a6530574a71bc1260920ce910c2292",
        },
        "io_grpc_grpc_protobuf": {
            "rule": "maven_jar",
            "artifact": "io.grpc:grpc-protobuf:1.10.0",
            "sha1": "64098f046f227b47238bc747e3cee6c7fc087bb8",
        },
        "io_grpc_grpc_protobuf_lite": {
            "rule": "maven_jar",
            "artifact": "io.grpc:grpc-protobuf-lite:1.10.0",
            "sha1": "b8e40dd308dc370e64bd2c337bb2761a03299a7f",
        },
        "io_opencensus_opencensus_contrib_grpc_metrics": {
            "rule": "maven_jar",
            "artifact": "io.opencensus:opencensus-contrib-grpc-metrics:0.11.0",
            "sha1": "d57b877f1a28a613452d45e35c7faae5af585258",
        },
        "io_grpc_grpc_stub": {
            "rule": "maven_jar",
            "artifact": "io.grpc:grpc-stub:1.10.0",
            "sha1": "d022706796b0820d388f83571da160fb8d280ded",
        },
        "io_grpc_grpc_all": {
            "rule": "maven_jar",
            "artifact": "io.grpc:grpc-all:1.10.0",
            "sha1": "efa94ac1ca079af730257d8ce24e8f120479eda3",
        },
        "com_google_auth_google_auth_library_credentials": {
            "rule": "maven_jar",
            "artifact": "com.google.auth:google-auth-library-credentials:0.9.1",
            "sha1": "25e0f45f3b3d1b4fccc8944845e51a7a4f359652",
        },
        "io_grpc_grpc_auth": {
            "rule": "maven_jar",
            "artifact": "io.grpc:grpc-auth:1.10.0",
            "sha1": "42fd0f1f757ef071acd20aaa6512272dc273068e",
        },
        "io_grpc_grpc_netty": {
            "rule": "maven_jar",
            "artifact": "io.grpc:grpc-netty:1.10.0",
            "sha1": "a1056d69003c9b46d1c4aa4a9167c6e8a714d152",
        },
        "io_grpc_grpc_okhttp": {
            "rule": "maven_jar",
            "artifact": "io.grpc:grpc-okhttp:1.10.0",
            "sha1": "2c9e1b901a53ca3176f3380204dbfad1e48c0ad4",
        },
        "io_grpc_grpc_protobuf_nano": {
            "rule": "maven_jar",
            "artifact": "io.grpc:grpc-protobuf-nano:1.10.0",
            "sha1": "3580b5f42c5fbf5cc7430ef3237feb7def4690de",
        },
    },
)

load("@org_pubref_rules_protobuf//python:rules.bzl", "py_proto_repositories")

py_proto_repositories()

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
    name = "io_bazel_rules_docker",
    sha256 = "29d109605e0d6f9c892584f07275b8c9260803bf0c6fcb7de2623b2bedc910bd",
    strip_prefix = "rules_docker-0.5.1",
    urls = ["https://github.com/bazelbuild/rules_docker/archive/v0.5.1.tar.gz"],
)

load(
    "@io_bazel_rules_docker//container:container.bzl",
    "container_pull",
    container_repositories = "repositories",
)

# This is NOT needed when going through the language lang_image
# "repositories" function(s).
container_repositories()

container_pull(
    name = "python36_docker",
    registry = "index.docker.io",
    repository = "library/python",
    # TODO: Change this back!!!
    tag = "3.6-alpine",
)

container_pull(
    name = "java8_docker",
    registry = "index.docker.io",
    repository = "library/openjdk",
    tag = "8-jdk-alpine",
)

load(
    "@io_bazel_rules_docker//python:image.bzl",
    _py_image_repos = "repositories",
)

_py_image_repos()
