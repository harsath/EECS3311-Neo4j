*** Settings ***
Library         RequestsLibrary
Test Timeout    30 seconds

Suite Setup     Create Session     localhost    http://localhost:8080
Suite Teardown  Delete All Sessions

*** Test Cases ***
addAwardTest1
    ${url}=         Set Variable   /api/v1/addAward?movieId=nm0000004&actorId=nm0000007&award=BestActor
    ${resp}=        GET On Session  localhost   ${url}  expected_status=200

addAwardTest2
    ${url}=         Set Variable   /api/v1/addAward?movieId=nm0000004&actorId=nm0090007&award=BestActor
    ${resp}=        GET On Session  localhost   ${url}  expected_status=404

addAwardTest3
    ${url}=         Set Variable   /api/v1/addAward?movieId=nm0090004&actorId=nm0000007&award=BestActor
    ${resp}=        GET On Session  localhost   ${url}  expected_status=404

addAwardTest4
    ${url}=         Set Variable   /api/v1/addAward?movieId=nm0000008&actorId=nm0000007&award=BestActor
    ${resp}=        GET On Session  localhost   ${url}  expected_status=400

addAwardTest5
    ${url}=         Set Variable   /api/v1/addAward?movieId=nm0000010
    ${resp}=        GET On Session  localhost   ${url}  expected_status=400

addAwardTest6
    ${url}=         Set Variable   /api/v1/addAward?award=BestActor
    ${resp}=        GET On Session  localhost   ${url}  expected_status=400

