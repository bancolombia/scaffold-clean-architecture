# We suggest to consulting the Karate Framework documentation: https://github.com/intuit/karate

Feature: Here is the full description of the test suite to be run DEMO

    How     ...
    Required ...
    To     ...


    Background:
    * url urlBase
    * configure headers = { 'Content-Type': 'application/json' }
    # * def json = read('classpath:more-json.json') -> you can declare a variable that calls a json to be used as request


    Scenario: This is the description of this scenario to be tested and its objective
        Given path 'health'
        When method get
        Then status 200
        And match response != 'Error'
        And match response contains { id: #notnull }

    Scenario Outline: This is the description of this scenario to be tested and its objective
        Given path 'health/<set>'
        When method get
        Then status 200
        And match $.response.country == '<res>'

        Examples:
        | set    | res   |
        | path1  | res1  |
        | path2  | res2  |
        | path3  | res3  |

    Scenario: This is the description of this scenario to be tested and its objective
        Given path 'health'
        And request { set: 'path1' }
        When method post
        Then status 201
        And match response == { id: '#number', set: '#(set)' }

    Scenario: This is the description of this scenario to be tested and its objective
        Given path 'health'
        And request read('my-json.json')
        When method put
        Then status 200
        And match header Content-Type == 'application/json'
        And match response.res == 'res1'
        And match response $.res == 'res1'
        And match $.res == 'res1'


### RECOMMENDATIONS:MATCHES are the most important in the tests, these assertions will allow us to corroborate that things are right or wrong according to validation.