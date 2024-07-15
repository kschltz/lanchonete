# lanchonete

Software criado para pós graduação FIAP

[![Coverage Status](https://coveralls.io/repos/github/kschltz/lanchonete/badge.svg?branch=feat/nats-posting)](https://coveralls.io/github/kschltz/lanchonete?branch=feat/nats-posting)

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/7462440-796f2919-cc85-47d9-af25-6d5b3828aa38?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D7462440-796f2919-cc85-47d9-af25-6d5b3828aa38%26entityType%3Dcollection%26workspaceId%3D89237b62-8986-4c78-81a4-725c13c2db8e#?env%5B%5Bdev%5D%20lanchonete%5D=W3sia2V5IjoiY2F0ZWdvcmlhIiwidmFsdWUiOiJsYW5jaGUiLCJlbmFibGVkIjp0cnVlLCJ0eXBlIjoiZGVmYXVsdCJ9LHsia2V5IjoiaG9zdCIsInZhbHVlIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwLyIsImVuYWJsZWQiOnRydWUsInR5cGUiOiJkZWZhdWx0In0seyJrZXkiOiJwcm9kdWN0X2lkIiwidmFsdWUiOiIiLCJlbmFibGVkIjp0cnVlLCJ0eXBlIjoiYW55In0seyJrZXkiOiJjbGllbnRlX2lkIiwidmFsdWUiOiIiLCJlbmFibGVkIjp0cnVlLCJ0eXBlIjoiZGVmYXVsdCJ9XQ==)

ps: caso tenha problema para rodar os testes diretamente pelo link acima, a collection pode ser acessada tbm por este [link](https://www.postman.com/cloudy-spaceship-8629/workspace/pos-fiap/collection/7462440-796f2919-cc85-47d9-af25-6d5b3828aa38?action=share&creator=7462440&active-environment=7462440-a3f44872-cf4b-4e3e-8a28-8a1dee94f395) ou ainda diretamente pelo arquivo neste [link](https://1drv.ms/f/s!ArHPKY_La1NFiKUY6SFT0c88iZI3mA?e=ZDs4fj)

obs: não esquecer de ativar o ambiente dev no postman

### Miro Board para Event Storming e Storytelling

[miro](https://miro.com/app/board/uXjVMg_qpOA=/?share_link_id=776193048301)

### Documentação de Rotas da aplicaçao

[doc](/doc/routes.md)


### Documentação do banco de dados

[doc](/doc/database.md)

## Installation

Download from https://github.com/kschltz/lanchonete

## Usage

### Kubernetes

You can run/deploy the project in Kubernetes using the following command:

    $ kubectl apply -f k8s/postgre/ && kubectl apply -f k8s/lanchonete/

#### kubernetes development

If you are using minikube you can run the following helper script to start a fresh minikube and load everything you need:

        $ ./bin/minikube-ground-up.sh

It will:

- 1 - Delete the current minikube
- 2 - Create a new minikube
- 3 - build the app image
- 4 - load de image
- 5 - apply the k8s files
- 7 - enable the metrics addon
- 8 - open up minikube tunnel for connectivity

##### You can see what the generated artifacts look like in the architecture diagrams:

[architecture documentation](/doc/architecture.md)

### Docker compose

From the root of the project, run:

    $ docker-compose up --build

### Clojure

Run the project in dev environment:

    $ clojure -A:dev:test

Run the project directly, via `:main-opts` (`-m mba-fiap.lanchonete`):

    $ clojure -M:run-m

Run the project's tests:

    $ clojure -T:build test

Run the project's CI pipeline and build an uberjar:

    $ clojure -T:build ci

This will produce an updated `pom.xml` file with synchronized dependencies inside the `META-INF`
directory inside `target/classes` and the uberjar in `target`. You can update the version (and SCM tag)
information in generated `pom.xml` by updating `build.clj`.

If you don't want the `pom.xml` file in your project, you can remove it. The `ci` task will
still generate a minimal `pom.xml` as part of the `uber` task, unless you remove `version`
from `build.clj`.

Run that uberjar:

    $ java -jar target/lanchonete-0.1.0-SNAPSHOT.jar

## ZAP OWASP scan

### 08/07/2024

#### Report /produtos/lanche
| Plugin | Strength | Progress | Elapsed | Reqs | Alerts | Status |
|--------|----------|----------|---------|------|--------|--------|
| Analyser | | 00:00.026 | 1 | | |
| Path Traversal | Medium | 100 | 00:00.051 | 0 | 0 | Completed |
| Remote File Inclusion | Medium | 100 | 00:00.030 | 0 | 0 | Completed |
| Source Code Disclosure - /WEB-INF Folder | Medium | 100 | 00:00.049 | 2 | 0 | Completed |
| Heartbleed OpenSSL Vulnerability | Medium | 100 | 00:00.038 | 3 | 0 | Completed |
| Source Code Disclosure - CVE-2012-1823 | Medium | 100 | 00:00.107 | 1 | 0 | Completed |
| Remote Code Execution - CVE-2012-1823 | Medium | 100 | 00:00.020 | 4 | 0 | Completed |
| External Redirect | Medium | 100 | 00:00.007 | 0 | 0 | Completed |
| Server Side Include | Medium | 100 | 00:00.012 | 0 | 0 | Completed |
| Cross Site Scripting (Reflected) | Medium | 100 | 00:00.009 | 0 | 0 | Completed |
| Cross Site Scripting (Persistent) - Prime | Medium | 100 | 00:00.007 | 0 | 0 | Completed |
| Cross Site Scripting (Persistent) - Spider | Medium | 100 | 00:00.035 | 2 | 0 | Completed |
| Cross Site Scripting (Persistent) | Medium | 100 | 00:00.006 | 0 | 0 | Completed |
| SQL Injection | Medium | 100 | 00:00.009 | 0 | 0 | Completed |
| SQL Injection - MySQL | Medium | 100 | 00:00.008 | 0 | 0 | Completed |
| SQL Injection - Hypersonic SQL | Medium | 100 | 00:00.008 | 0 | 0 | Completed |
| SQL Injection - Oracle | Medium | 100 | 00:00.012 | 0 | 0 | Completed |
| SQL Injection - PostgreSQL | Medium | 100 | 00:00.010 | 0 | 0 | Completed |
| SQL Injection - SQLite | Medium | 100 | 00:00.010 | 0 | 0 | Completed |
| Cross Site Scripting (DOM Based) | Medium | 100 | 02:10.698 | 0 | 0 | Skipped, failed to start or connect to the browser. |
| SQL Injection - MsSQL | Medium | 100 | 00:00.058 | 0 | 0 | Completed |
| Log4Shell | Medium | 100 | 00:00.016 | 0 | 0 | Skipped, no Active Scan OAST service is selected.. |
| Spring4Shell | Medium | 100 | 00:00.113 | 4 | 0 | Completed |
| Server Side Code Injection | Medium | 100 | 00:00.022 | 0 | 0 | Completed |
| Remote OS Command Injection | Medium | 100 | 00:00.019 | 0 | 0 | Completed |
| XPath Injection | Medium | 100 | 00:00.018 | 0 | 0 | Completed |
| XML External Entity Attack | Medium | 100 | 00:00.017 | 0 | 0 | Completed |
| Generic Padding Oracle | Medium | 100 | 00:00.011 | 0 | 0 | Completed |
| Cloud Metadata Potentially Exposed | Medium | 100 | 00:00.033 | 4 | 0 | Completed |
| Server Side Template Injection | Medium | 100 | 00:00.027 | 0 | 0 | Completed |
| Server Side Template Injection (Blind) | Medium | 100 | 00:00.012 | 0 | 0 | Completed |
| Directory Browsing | Medium | 100 | 00:00.015 | 2 | 0 | Completed |
| Buffer Overflow | Medium | 100 | 00:00.007 | 0 | 0 | Completed |
| Format String Error | Medium | 100 | 00:00.015 | 0 | 0 | Completed |
| CRLF Injection | Medium | 100 | 00:00.011 | 0 | 0 | Completed |
| Parameter Tampering | Medium | 100 | 00:00.008 | 0 | 0 | Completed |
| ELMAH Information Leak | Medium | 100 | 00:00.011 | 1 | 0 | Completed |
| Trace.axd Information Leak | Medium | 100 | 00:00.019 | 2 | 0 | Completed |
| .htaccess Information Leak | Medium | 100 | 00:00.017 | 2 | 0 | Completed |
| .env Information Leak | Medium | 100 | 00:00.021 | 2 | 0 | Completed |
| Spring Actuator Information Leak | Medium | 100 | 00:00.050 | 2 | 0 | Completed |
| Hidden File Finder | Medium | 100 | 00:00.403 | 50 | 0 | Completed |
| XSLT Injection | Medium | 100 | 00:00.363 | 0 | 0 | Completed |
| GET for POST | Medium | 100 | 00:00.009 | 0 | 0 | Completed |
| User Agent Fuzzer | Medium | 100 | 00:00.361 | 24 | 0 | Completed |
| Script Active Scan Rules | Medium | 100 | 00:00.009 | 0 | 0 | Skipped, no scripts enabled. |
| SOAP Action Spoofing | Medium | 100 | 00:00.005 | 0 | 0 | Completed |
| SOAP XML Injection | Medium | 100 | 00:00.006 | 0 | 0 | Completed |
| Totals | | 02:12.375 | 153 | 0 | |

## License

Copyright © 2023 Kschltz

_EPLv1.0 is just the default for projects generated by `clj-new`: you are not_
_required to open source this project, nor are you required to use EPLv1.0!_
_Feel free to remove or change the `LICENSE` file and remove or update this_
_section of the `README.md` file!_

Distributed under the Eclipse Public License version 1.0.
