USER="user123"
PASSWORD="Admin123"
URI_ENDPOINT="/api/some-v1/login"
URI="https://ulia-dev.com"
PORT="1443"
#=======================================================================================================================
curl -X 'POST' \
  "$URI:$PORT$URI_ENDPOINT" \
  -H 'Accept: application/json' \
  -H 'Content-Type: */*' \
  -d "{
  \"requestBodyDto\": {
    \"userLogin\": \"$USER\",
    \"userPass\": \"$PASSWORD\"
  }
}" -vvv