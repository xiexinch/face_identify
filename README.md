### Face_v

A human face identify android application

#### Server

We use NodeJs as server. The server resource code refer to the project [face_server](https://github.com/xiexinch/face_servr_v2)

#### 功能点

- 人脸搜索：单人和多人
- 人脸标注：搜索完成后会在图片中标注出人脸和搜索结果
- 人脸库管理：添加和删除
- 照片墙：展示人脸库中用户上传的照片

#### 数据库设计

我们在NodeJS的服务端设计了一个简单的数据库,用于存储添加人脸的记录。数据库系统使用了MongoDB数据库，在NodeJS服务端用Mongoose数据工具连接和操作数据库。

**Face**

|  Key| Type |
| --- | --- |
| uuid | String |
| user_info | String |
| urls | \[String\] |

#### 单元测试设计
在单元测试中，我们主要对**网络请求、JSON解析**功能进行了测试。
```
/**
* 网络连接的测试
*/
    @Test
    public void testConnect() {

        OkHttpClient client = new OkHttpClient();

        Request requestBody = new Request.Builder()
                .url("http://localhost:8000/face/hello")
                .get()
                .build();

        try {
            Response response = client.newCall(requestBody).execute();
            System.out.println(response.body().string());

        }catch (IOException e) {
            e.printStackTrace();
        }

    }
```

```

/**
* 解析JSON对象测试
*/
    @Test
    public void test2() {
        String response = "[{\"face_token\":\"873550c5a57c93f84b5c71094d76182f\",\"location\":{\"left\":518.66,\"top\":516.13,\"width\":275,\"height\":275,\"rotation\":72},\"user_list\":[{\"group_id\":\"lxh1\",\"user_id\":\"f85c4f19b0bc44e79ca9606329d8a341\",\"user_info\":\"谢昕辰\",\"score\":91.280830383301}]},{\"face_token\":\"f8f0b04ab19583ee4498890ce86e2756\",\"location\":{\"left\":488.99,\"top\":304.14,\"width\":210,\"height\":234,\"rotation\":128},\"user_list\":[{\"group_id\":\"lxh1\",\"user_id\":\"4bfea364b5b0409ba88b80758f4ce100\",\"user_info\":\"李浩\",\"score\":95.890327453613}]}]";
        List list = JSON.parseArray(response);
        Iterator it  = list.iterator();
        while (it.hasNext()) {
            Face face = JSON.parseObject(it.next().toString(), Face.class);
            System.out.println(face.getFace_token());
            System.out.println(face.getLocation().getHeight());
            List<User> userList = face.getUser_list();
            if (!userList.isEmpty()) {
                Iterator userIt = userList.iterator();
                while ((userIt.hasNext())) {
                    System.out.println(((User)userIt.next()).getUser_info());
                }
            }
        }
    }

```

#### 类设计

#### 本地接口设计

#### restful网络接口设计
我们在NodeJS服务端开放了几个网络接口用于实现人脸识别搜索、记录人脸添加、人脸照片墙功能。

| 接口 | 说明 | 请求方式 |
| --- | --- | --- |
| http://121.199.23.49:8001/face/ | 安卓程序的webview访问页面 | GET |
| http://121.199.23.49:8001/face/search_face | 人脸识别 | POST |
| http://121.199.23.49:8001/face/add_face | 添加人脸 | POST
| http://121.199.23.49:8001/face/get_faces_info | 获取人脸库信息 | GET
| http://121.199.23.49:8001/face/deleteUser | 删除人脸 | GET

#### 软件安装及使用说明

