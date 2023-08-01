*** Settings ***
Library         Collections
Library         RequestsLibrary
Test Timeout    30 seconds

Suite Setup    Create Session    localhost    http://localhost:8080

*** Test Cases ***
addMoviePass
    ${headers}=     Create Dictionary   Content-Type=application/json
    ${params}=      Create Dictionary   name=Titanic  movieId=nm0000002
    ${resp}=        PUT On Session      localhost   /api/v1/addMovie    json=${params}  headers=${headers}  expected_status=200

addMovieFail1
    ${headers}=     Create Dictionary   Content-Type=application/json
    ${params}=      Create Dictionary   name=Titanic
    ${resp}=        PUT On Session      localhost   /api/v1/addMovie    json=${params}  headers=${headers}  expected_status=400

addMovieFail2
    ${headers}=     Create Dictionary   Content-Type=application/json
    ${params}=      Create Dictionary   name=Titanic  movieId=nm0000002
    ${resp}=        PUT On Session      localhost   /api/v1/addMovie    json=${params}  headers=${headers}  expected_status=400
