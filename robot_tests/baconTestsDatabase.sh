#!/usr/bin/env bash

actorList=(
    "nm0000001 Brad Pitt"
    "nm0000002 Will Smith"
    "nm0000003 Johnny Depp"
    "nm0000004 Tom Cruise"
    "nm0000005 Christian Bale"
    "nm0000006 Bradley Cooper"
    "nm0000007 Robert Downey"
    "nm0000011 Keanu Reeves"
    "nm0000102 Kevin Bacon"
)
movieList=(
    "nm0000008 Fight Club"
    "nm0000009 American Psycho"
    "nm0000010 Pirates of the Caribbean"
    "nm0000004 Pursuit of Happyness"
    "nm0000005 Iron Man"
    "nm0000006 Top Gun"
)
relationshipList=(
    "nm0000001 nm0000008"
    "nm0000002 nm0000004"
    "nm0000003 nm0000010"
    "nm0000004 nm0000006"
    "nm0000005 nm0000009"
    "nm0000006 nm0000008"
    "nm0000007 nm0000005"
    "nm0000102 nm0000006"
    "nm0000004 nm0000009"
    "nm0000001 nm0000010"
    "nm0000005 nm0000008"
    "nm0000007 nm0000004"
)

curl http://localhost:8080/api/v1/clearDatabase

for actorListPair in "${actorList[@]}"; do
    IFS=' ' read -r actorId actorName <<< "$actorListPair"
    curl -X PUT http://localhost:8080/api/v1/addActor --data "{\"name\": \"$actorName\", \"actorId\": \"$actorId\"}"
done
for movieListPair in "${movieList[@]}"; do
    IFS=' ' read -r movieId movieName <<< "$movieListPair"
    curl -X PUT http://localhost:8080/api/v1/addMovie --data "{\"name\": \"$movieName\", \"movieId\": \"$movieId\"}"
done
for relationshipListPair in "${relationshipList[@]}"; do
    IFS=' ' read -r actorId movieId <<< "$relationshipListPair"
    curl -X PUT http://localhost:8080/api/v1/addRelationship --data "{\"actorId\": \"$actorId\", \"movieId\": \"$movieId\"}"
done
