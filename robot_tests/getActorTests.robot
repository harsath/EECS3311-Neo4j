*** Settings ***
Library         RequestsLibrary
Test Timeout    30 seconds

Suite Setup     Create Session     localhost    http://localhost:8080
Suite Teardown  Delete All Sessions

*** Test Cases ***
getActorTest1
    ${url}=         Set Variable   /api/v1/getActor?actorId=nm0000005
    ${resp}=        GET On Session  localhost   ${url}  expected_status=200

getActorTest2
    ${url}=         Set Variable   /api/v1/getActor?actorId=nm0000011
    ${resp}=        GET On Session  localhost   ${url}  expected_status=200

getActorTest3
    ${url}=         Set Variable   /api/v1/getActor?actorId=nm0000090
    ${resp}=        GET On Session  localhost   ${url}  expected_status=404

getActorTest4
    ${url}=         Set Variable   /api/v1/getActor?actor=nm0000005
    ${resp}=        GET On Session  localhost   ${url}  expected_status=400
