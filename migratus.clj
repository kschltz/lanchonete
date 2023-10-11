{:store         :database
 :migration-dir "migrations"
 :db            (or
                  (get (System/getenv) "JDBC_DATABASE_URL")
                  {:classname   "org.postgresql.driver"
                   :subprotocol "postgresql"
                   :subname     (or (get (System/getenv) "DB_SPEC__SUBNAME") "//localhost:9000/")
                   :user        (or (get (System/getenv) "DB_SPEC__USER") "lanchonete-user")
                   :password    (or (get (System/getenv) "DB_SPEC__PASSWORD") "lanchonete-pass")})}
