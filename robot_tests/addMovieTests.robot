*** Settings ***
Library         Collections
Library         RequestsLibrary
Test Timeout    30 seconds

Suite Setup    Create Session    localhost    http://localhost:8080
Suite Teardown  Delete All Sessions

*** Test Cases ***
addMovieTest1
    ${headers}=     Create Dictionary   Content-Type=application/json
    ${params}=      Create Dictionary   name=Fight Club  movieId=nm0000002
    ${resp}=        PUT On Session      localhost   /api/v1/addMovie    json=${params}  headers=${headers}  expected_status=200

addMovieTest2
    ${headers}=     Create Dictionary   Content-Type=application/json
    ${params}=      Create Dictionary   name=Fight Club
    ${resp}=        PUT On Session      localhost   /api/v1/addMovie    json=${params}  headers=${headers}  expected_status=400

addMovieTest3
    ${headers}=     Create Dictionary   Content-Type=application/json
    ${params}=      Create Dictionary   name=Fight Club  movieId=nm0000002
    ${resp}=        PUT On Session      localhost   /api/v1/addMovie    json=${params}  headers=${headers}  expected_status=400
