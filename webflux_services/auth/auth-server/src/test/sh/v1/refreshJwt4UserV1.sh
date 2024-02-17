REFRESH_TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJVU0VSX0xPR0lOIjoidXNlcjI0IiwiVE9LRU5fS0lORCI6IlJFRlJFU0hfVE9LRU4iLCJzdWIiOiJvcmcuZGJzLmF1dGguc2VydmVyLmNsaWVudHMudjEuc2VydmljZSIsImlhdCI6MTcwMjM2NjQ0NywiZXhwIjoxNzAyMzY4MjQ5fQ.vwc1vzVxafd1mBtHhnQJ0gsrD4C2YmzxdWJZ39LNC8s"
ACCESS_TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJVU0VSX1BIT05FIjoiKzc4ODg4ODg4ODg4IiwiVVNFUl9GSVJTVF9OQU1FIjoiQWxla3NleSIsIlNDSE9PTF9ST0xFUyI6IlJPTEVfQURNSU4sIFJPTEVfU1RBRkYsIFJPTEVfUEFSRU5ULCBCYXNpYywgQWxsUHJpdmlsZWdlcyIsIlVTRVJfTE9HSU4iOiJ1c2VyMjQiLCJVU0VSX0VNQUlMIjoic2Rmc2Rmc2RmQHNka2ZzbGtkZi5jb20iLCJVU0VSX0lEIjoiMjMiLCJTQ0hPT0xfVFoiOiIxODAiLCJVU0VSX0xBU1RfTkFNRSI6IllhdHNlbmtvIiwiVVNFUl9BRERSRVNTIjoiQmVsYXJ1cyxNaW5zayxTYWRvdmF5YSwzMzMzMzMsRmRzLDExLDExIiwiU0NIT09MX0lEIjoiMSIsIlRPS0VOX0tJTkQiOiJBQ0NFU1NfVE9LRU4iLCJzdWIiOiJvcmcuZGJzLmF1dGguc2VydmVyIiwiaWF0IjoxNzAyMzY2NDQ3LCJleHAiOjE3MDIzNjY1MDl9.u_nXEAf_fT8E2L07nBF_r7skzbwlHYwPO3NskFae66I"
URI_ENDPOINT="/api/some-v1/jwt/refresh"
URI="https://ulia-dev.com"
PORT="1443"
#=======================================================================================================================
curl -X 'POST' \
  "$URI:$PORT$URI_ENDPOINT" \
  -H 'Accept: application/json' \
  -H "Authorization: Bearer $REFRESH_TOKEN" \
  -H 'Content-Type: */*' \
  -d "{
   \"requestBodyDto\": {
    \"expiredJwt\": \"$ACCESS_TOKEN\",
    \"refreshJwt\": \"$REFRESH_TOKEN\"
  }
}" -vvv
