this spring-boot project use httpclient execute post Or get request access the elaticsearch

without elaticsearch java api ,
and the postJson is define different by you esColumn type

if your esColumn type is text ,the project will be ok,
else if your esColumn type is keyword ,the postJson will use term not queryString
