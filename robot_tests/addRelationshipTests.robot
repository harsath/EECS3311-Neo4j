*** Settings ***
Library         Collections
Library         RequestsLibrary
Test Timeout    30 seconds

Suite Setup    Create Session    localhost    http://localhost:8080
Suite Teardown  Delete All Sessions

*** Test Cases ***
addRelationshipPass
    ${headers}=     Create Dictionary   Content-Type=application/json
    ${params}=      Create Dictionary   actorId=nm0000001  movieId=nm0000002
    ${resp}=        PUT On Session      localhost   /api/v1/addRelationship    json=${params}  headers=${headers}  expected_status=200

addRelationshipFail1
    ${headers}=     Create Dictionary   Content-Type=application/json
    ${params}=      Create Dictionary   actorId=nm0000001
    ${resp}=        PUT On Session      localhost   /api/v1/addRelationship    json=${params}  headers=${headers}  expected_status=400

addRelationshipFail2
    ${headers}=     Create Dictionary   Content-Type=application/json
    ${params}=      Create Dictionary   actorId=nm0000001  movieId=nm0000002
    ${resp}=        PUT On Session      localhost   /api/v1/addRelationship    json=${params}  headers=${headers}  expected_status=400
