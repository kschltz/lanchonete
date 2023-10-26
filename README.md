# lanchonete

Software criado para pós graduação FIAP

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/7462440-796f2919-cc85-47d9-af25-6d5b3828aa38?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D7462440-796f2919-cc85-47d9-af25-6d5b3828aa38%26entityType%3Dcollection%26workspaceId%3D89237b62-8986-4c78-81a4-725c13c2db8e#?env%5B%5Bdev%5D%20lanchonete%5D=W3sia2V5IjoiY2F0ZWdvcmlhIiwidmFsdWUiOiJsYW5jaGUiLCJlbmFibGVkIjp0cnVlLCJ0eXBlIjoiZGVmYXVsdCJ9LHsia2V5IjoiaG9zdCIsInZhbHVlIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwLyIsImVuYWJsZWQiOnRydWUsInR5cGUiOiJkZWZhdWx0In0seyJrZXkiOiJwcm9kdWN0X2lkIiwidmFsdWUiOiIiLCJlbmFibGVkIjp0cnVlLCJ0eXBlIjoiYW55In0seyJrZXkiOiJjbGllbnRlX2lkIiwidmFsdWUiOiIiLCJlbmFibGVkIjp0cnVlLCJ0eXBlIjoiZGVmYXVsdCJ9XQ==)

ps: caso tenha problema para rodar os testes diretamente pelo link acima, a collection pode ser acessada tbm por este [link](https://www.postman.com/cloudy-spaceship-8629/workspace/pos-fiap/collection/7462440-796f2919-cc85-47d9-af25-6d5b3828aa38?action=share&creator=7462440&active-environment=7462440-a3f44872-cf4b-4e3e-8a28-8a1dee94f395)

obs: não esquecer de ativar o ambiente dev no postman

### Miro Board para Event Storming e Storytelling

[miro](https://miro.com/app/board/uXjVMg_qpOA=/?share_link_id=776193048301)

## Installation

Download from https://github.com/mba-fiap/lanchonete

## Usage

Run the project in dev environment:

    $ clojure -A:dev:test

Run the project directly, via `:exec-fn`:

    $ clojure -X:run-x
    Hello, Clojure!

Run the project, overriding the name to be greeted:

    $ clojure -X:run-x :name '"Someone"'
    Hello, Someone!

Run the project directly, via `:main-opts` (`-m mba-fiap.lanchonete`):

    $ clojure -M:run-m
    Hello, World!

Run the project, overriding the name to be greeted:

    $ clojure -M:run-m Via-Main
    Hello, Via-Main!

Run the project's tests (they'll fail until you edit them):

    $ clojure -T:build test

Run the project's CI pipeline and build an uberjar (this will fail until you edit the tests to pass):

    $ clojure -T:build ci

This will produce an updated `pom.xml` file with synchronized dependencies inside the `META-INF`
directory inside `target/classes` and the uberjar in `target`. You can update the version (and SCM tag)
information in generated `pom.xml` by updating `build.clj`.

If you don't want the `pom.xml` file in your project, you can remove it. The `ci` task will
still generate a minimal `pom.xml` as part of the `uber` task, unless you remove `version`
from `build.clj`.

Run that uberjar:

    $ java -jar target/lanchonete-0.1.0-SNAPSHOT.jar

## Examples

...

### Bugs

...

### Any Other Sections

### That You Think

### Might be Useful

## License

Copyright © 2023 Kschltz

_EPLv1.0 is just the default for projects generated by `clj-new`: you are not_
_required to open source this project, nor are you required to use EPLv1.0!_
_Feel free to remove or change the `LICENSE` file and remove or update this_
_section of the `README.md` file!_

Distributed under the Eclipse Public License version 1.0.
