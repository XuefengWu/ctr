ctr: csv test rest
===

Usage
---

config the url root at **conf/application.conf**

`
url.root = https://api.github.com
`

put github.csv in **csv** folder:

messaage|URL API | Method | Input | Status Code | Output
:-------|:-------| :-----: | :---- | :----------: | :---------
get user infor|/user         | get        | |401 |{"message": "Requires authentication"}
create issue | /repos/XuefengWu/ctr/issues         | post        |{"title":"Found a Bug"} |404 |{"message": "Not Found"}
get issue infor | /repos/octocat/Hello-World/issues/1         | get        | |200 |id,title,url,state,assignee


`
$./sbt test
`


Matcher
---

**Status Code is equal**

send a get request and except the status code.

**example:** 

URL API | Method | Input | Status Code | Output
:-------| :-----: | :---- | :----------: | :---------
/user         | get        | |**401** |{"message": "Requires authentication"}

**Json value is equals**

post json body and except the result json value

**example:** 

URL API | Method | Input | Status Code | Output
:-------| :-----: | :---- | :----------: | :---------
/repos/XuefengWu/ctr/issues         | post        |{"title":"Found a Bug"} |404 |**{"message": "Not Found"}**


**Contain Json Node**

get a json result and assert if the json node is existed.

**example:** 

URL API | Method | Input | Status Code | Output
:-------| :-----: | :---- | :----------: | :---------
/repos/octocat/Hello-World/issues/1         | get        | |200 |id,title,url,state,assignee

 
