(ns mba-fiap.value-object.cpf-test
  (:require [clojure.test :refer :all]
            [mba-fiap.value-object.cpf :refer [valid? gen-cpf]]))

(deftest cpf-validation
  (testing "valid CPF"
    (is (valid? "22768159492"))
    (is (valid? "227.681.594-92")))
  (testing "invalid CPF"
    (is (not (valid? "04373360188")))
    (is (not (valid? "043.733.601-88"))))
  (testing "Every generated cpf is valid"
    (is (every? valid? (repeatedly 1000 gen-cpf)))))
