*** Settings ***
Library         RequestsLibrary
Test Timeout    30 seconds

Suite Setup     Create Session     localhost    http://localhost:8080
Suite Teardown  Delete All Sessions

*** Test Cases ***
getMovieTest1
    ${url}=         Set Variable   /api/v1/getMovie?movieId=nm0000008
    ${resp}=        GET On Session  localhost   ${url}  expected_status=200

getMovieTest2
    ${url}=         Set Variable   /api/v1/getMovie?movieId=nm0000012
    ${resp}=        GET On Session  localhost   ${url}  expected_status=200

getMovieTest3
    ${url}=         Set Variable   /api/v1/getMovie?movieId=nm0000112
    ${resp}=        GET On Session  localhost   ${url}  expected_status=404

getMovieTest4
    ${url}=         Set Variable   /api/v1/getMovie?movie=nm0000008
    ${resp}=        GET On Session  localhost   ${url}  expected_status=400
