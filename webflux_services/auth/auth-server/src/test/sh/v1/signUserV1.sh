USER="user24"
PASSWORD="Admin123"
URI_ENDPOINT="/api/auth/sign-in"
URI="https://admin.com"
PARAMS="?login=$USER&password=$PASSWORD"
PORT="443"
#=======================================================================================================================
curl -X 'POST' \
  "$URI:$PORT$URI_ENDPOINT$PARAMS" \
  -H 'Accept: application/json' \
  -H 'Content-Type: */*' \
  -vvv
