#!/bin/bash

URI_ENDPOINT="/api/some/v1/login"
PORT="1444"
TMPFILE=~/.$SOME_USER.jwt

curl -X 'POST' \
  "$URI:$PORT$URI_ENDPOINT" \
  -H 'Accept: application/json' \
  -H 'Content-Type: */*' \
  -d "{
  \"version\": \"string\",
  \"entityAction\": {},
  \"entityInfo\": {
    \"appPackage\": \"not defined\",
    \"userLogin\": \"$SOME_USER\",
    \"userPass\": \"$SOME_PASSWORD\",
    \"appVersion\": \"0.0.0\"
  }
}" >$TMPFILE

PARSED_KEY="\"jwt\""
PARSED_EXPR="$PARSED_KEY:\"[^\"]*"
PARSED_VALUE='[^"]*$'
JWT=$(grep -o $PARSED_EXPR $TMPFILE | grep -o $PARSED_VALUE)

if [[ ${#JWT} == 0 ]]; then
  PARSED_KEY="\"code\""
  PARSED_EXPR="$PARSED_KEY:\"[^\"]*"
  PARSED_VALUE='[^"]*$'

  PARSED_KEY="\"error\""
  PARSED_EXPR="$PARSED_KEY:\"[^\"]*"
  PARSED_VALUE='[^"]*$'
  ERROR=$(grep -o $PARSED_EXPR $TMPFILE | grep -o $PARSED_VALUE)

  RED_FONT='\033[0;31m'
  echo -e "${RED_FONT}!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  echo -e "### CANNOT RECEIVE JWT FROM \"$URI:$PORT$URI_ENDPOINT\""
  echo -e "### ERROR:\"$ERROR\""

fi

export JWT
