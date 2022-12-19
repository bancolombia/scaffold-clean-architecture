# We suggest to consulting the Karate Framework documentation: https://github.com/intuit/karate
@acceptanceTest
Feature: Here is the full description of the test suite to be run DEMO

    How     ...
    Required ...
    To     ...


    Background:
        # This property is taken from the karate-config.js file
        * url urlBase
        # This property is taken from the karate-config.js file
        * def oasUrl = oasUrl
        # This property is taken from the karate-config.js file
        * configure headers = headers
        * def ValidatorTestUtils = Java.type('co.com.bancolombia.utils.ValidatorTestUtils')
    # * def json = read('classpath:more-json.json') -> you can declare a variable that calls a json to be used as request


    #   Reference
    #   https://github.com/karatelabs/karate#request
    #   https://github.com/karatelabs/karate#reading-files
    Scenario: This is the description of this scenario to be tested and its objective
        Given path 'pet'
        And request read('addPet.json')
        When method post
        Then status 200
        * string strResponse = response
        * def report = ValidatorTestUtils.validateResponseSchema(oasUrl, strResponse, 'pet', ValidatorTestUtils.POST, 200)
        * if (report.hasErrors()) karate.fail(report.getMessages() + ' - '+ strResponse)

    #   Reference
    #   https://github.com/karatelabs/karate#path
    #   https://github.com/karatelabs/karate#param
    #   https://github.com/karatelabs/karate#params
    Scenario Outline: Update pet with id <PET_ID>
        Given path 'pet/', <PET_ID>
        And params {name: '<NAME>', status: '<STATUS>'}
        When method post
        Then status 200
        * string strResponse = response
        * def report = ValidatorTestUtils.validateResponseSchema(oasUrl, strResponse, 'pet/{petId}', ValidatorTestUtils.POST, 200)
        * if (report.hasErrors()) karate.fail(report.getMessages() + ' - '+ strResponse)

        Examples:
            | PET_ID | NAME   | STATUS    |
            | 9      | Canelo | available |
            | 10     | Alice  | available |

    #   Reference
    #   https://github.com/karatelabs/karate#path
    Scenario: Get pet with id 9
        Given path 'pet/9'
        And configure headers = { 'Content-Type': 'application/json' }
        When method get
        Then status 200
        And match header Content-Type == 'application/json'
        And match response.id == 9


### RECOMMENDATIONS:MATCHES are the most important in the tests, these assertions will allow us to corroborate that things are right or wrong according to validation.

### RECOMMENDATIONS: The class ValidatorTestUtils provides a powerfull tool to carry out schema validations using the Open API Specification of the API to be tested