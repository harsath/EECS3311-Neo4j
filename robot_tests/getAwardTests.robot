*** Settings ***
Library         RequestsLibrary
Test Timeout    30 seconds

Suite Setup     Create Session     localhost    http://localhost:8080
Suite Teardown  Delete All Sessions

*** Test Cases ***
getAwardTest1
    ${url}=         Set Variable   /api/v1/getAward?movieId=nm0000004&award=BestActor
    ${resp}=        GET On Session  localhost   ${url}  expected_status=200

getAwardTest2
    ${url}=         Set Variable   /api/v1/getAward?movieId=nm0000010&award=BestActor
    ${resp}=        GET On Session  localhost   ${url}  expected_status=200

getAwardTest3
    ${url}=         Set Variable   /api/v1/getAward?movieId=nm0000910&award=BestActor
    ${resp}=        GET On Session  localhost   ${url}  expected_status=404

getAwardTest4
    ${url}=         Set Variable   /api/v1/getAward?movieId=nm0000010
    ${resp}=        GET On Session  localhost   ${url}  expected_status=400

getAwardTest5
    ${url}=         Set Variable   /api/v1/getAward?award=BestActor
    ${resp}=        GET On Session  localhost   ${url}  expected_status=400

getAwardTest6
    ${url}=         Set Variable   /api/v1/getAward?award=BestActor&actorId=nm0000010
    ${resp}=        GET On Session  localhost   ${url}  expected_status=400
