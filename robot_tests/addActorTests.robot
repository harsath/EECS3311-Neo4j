*** Settings ***
Library         Collections
Library         RequestsLibrary
Test Timeout    30 seconds

Suite Setup    Create Session    localhost    http://localhost:8080
Suite Teardown  Delete All Sessions

*** Test Cases ***
addActorTest1
    ${headers}=     Create Dictionary   Content-Type=application/json
    ${params}=      Create Dictionary   name=Brad Pitt  actorId=nm0000001
    ${resp}=        PUT On Session      localhost   /api/v1/addActor    json=${params}  headers=${headers}  expected_status=200

addActorTest1
    ${headers}=     Create Dictionary   Content-Type=application/json
    ${params}=      Create Dictionary   name=Bradley Cooper  actorId=nm0000003
    ${resp}=        PUT On Session      localhost   /api/v1/addActor    json=${params}  headers=${headers}  expected_status=200

addActorTest2
    ${headers}=     Create Dictionary   Content-Type=application/json
    ${params}=      Create Dictionary   name=Brad Pitt
    ${resp}=        PUT On Session      localhost   /api/v1/addActor    json=${params}  headers=${headers}  expected_status=400

addActorTest3
    ${headers}=     Create Dictionary   Content-Type=application/json
    ${params}=      Create Dictionary   name=Brad Pitt  actorId=nm0000001
    ${resp}=        PUT On Session      localhost   /api/v1/addActor    json=${params}  headers=${headers}  expected_status=400
