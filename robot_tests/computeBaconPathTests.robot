*** Settings ***
Library         RequestsLibrary
Test Timeout    30 seconds

Suite Setup     Create Session     localhost    http://localhost:8080
Suite Teardown  Delete All Sessions

*** Test Cases ***
computeBaconPathTest1
    ${url}=         Set Variable   /api/v1/computeBaconPath?actorId=nm0000005
    ${resp}=        GET On Session  localhost   ${url}  expected_status=200

computeBaconPathTest2
    ${url}=         Set Variable   /api/v1/computeBaconPath?actorId=nm0000090
    ${resp}=        GET On Session  localhost   ${url}  expected_status=404

computeBaconPathTest3
    ${url}=         Set Variable   /api/v1/computeBaconPath?actorId=nm0000102
    ${resp}=        GET On Session  localhost   ${url}  expected_status=200

computeBaconPathTest4
    ${url}=         Set Variable   /api/v1/computeBaconPath?actor=nm0000005
    ${resp}=        GET On Session  localhost   ${url}  expected_status=400

computeBaconPathTest5
    ${url}=         Set Variable   /api/v1/computeBaconPath?actorId=nm0000002
    ${resp}=        GET On Session  localhost   ${url}  expected_status=404
