*** Settings ***
Library         RequestsLibrary
Test Timeout    30 seconds

Suite Setup     Create Session     localhost    http://localhost:8080
Suite Teardown  Delete All Sessions

*** Test Cases ***
computeBaconNumberTest1
    ${url}=         Set Variable   /api/v1/computeBaconNumber?actorId=nm0000005
    ${resp}=        GET On Session  localhost   ${url}  expected_status=200

computeBaconNumberTest2
    ${url}=         Set Variable   /api/v1/computeBaconNumber?actorId=nm0000090
    ${resp}=        GET On Session  localhost   ${url}  expected_status=404

computeBaconNumberTest3
    ${url}=         Set Variable   /api/v1/computeBaconNumber?actorId=nm0000102
    ${resp}=        GET On Session  localhost   ${url}  expected_status=200

computeBaconNumberTest4
    ${url}=         Set Variable   /api/v1/computeBaconNumber?actor=nm0000005
    ${resp}=        GET On Session  localhost   ${url}  expected_status=400

computeBaconNumberTest5
    ${url}=         Set Variable   /api/v1/computeBaconNumber?actorId=nm0000002
    ${resp}=        GET On Session  localhost   ${url}  expected_status=404
