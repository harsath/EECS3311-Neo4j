*** Settings ***
Library         RequestsLibrary
Test Timeout    30 seconds

Suite Setup     Create Session     localhost    http://localhost:8080
Suite Teardown  Delete All Sessions

*** Test Cases ***
hasRelationshipTest1
    ${url}=         Set Variable   /api/v1/hasRelationship?actorId=nm0000001&movieId=nm0000002
    ${resp}=        GET On Session  localhost   ${url}  expected_status=200

hasRelationshipTest2
    ${url}=         Set Variable   /api/v1/hasRelationship?actorId=nm0000003&movieId=nm0000002
    ${resp}=        GET On Session  localhost   ${url}  expected_status=200

hasRelationshipTest3
    ${url}=         Set Variable   /api/v1/hasRelationship?actorId=nm0000050&movieId=nm0000002
    ${resp}=        GET On Session  localhost   ${url}  expected_status=404

hasRelationshipTest4
    ${url}=         Set Variable   /api/v1/hasRelationship?actorId=nm0000001&movieId=nm0000050
    ${resp}=        GET On Session  localhost   ${url}  expected_status=404

hasRelationshipTest5
    ${url}=         Set Variable   /api/v1/hasRelationship?movieId=nm0000002
    ${resp}=        GET On Session  localhost   ${url}  expected_status=400

hasRelationshipTest6
    ${url}=         Set Variable   /api/v1/hasRelationship?actorId=nm0000001
    ${resp}=        GET On Session  localhost   ${url}  expected_status=400
