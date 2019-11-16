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

##### Activity类

###### MainActivity

实现选择，拍照，上传，添加到人脸库方法

###### InfoActivity

加载url和myService实现WebView混合开发的android端

##### Adapter类
Data和UI之间的桥梁

###### UserAdapter

实现用户列表，列表元素获取方法

###### FaceInfoAdapter

实现面部信息列表

##### Entity类
解析返回的json对象

###### FaceEntity

获取json对象中的face_token,location,user_List

###### LocationEntity

获取location中的位置参数

###### UserEntity

获取group_id,user_id,user_info,score

##### WebService类

###### MyService

web混合开发安卓端方法实现代码

#### 本地接口设计
| 接口 | 说明 | 参数 | 
| --- | --- | --- |
| deletUser | 删除人脸库用户 | 用户id （String user_id)，groupid(String group_id) |
| addface  | 添加人脸 | 图片（BASE64）,图片类型（String image_type),人脸库组（String group_id_list） | 
| getUserList | 获取人脸库用户列表 | 人脸库组（String group_id） |


#### restful网络接口设计

我们在NodeJS服务端开放了几个网络接口用于实现人脸识别搜索、记录人脸添加、人脸照片墙功能。

| 接口 | 说明 | 请求方式 |
| --- | --- | --- |
| http://192.168.1.103:8001/face/ | 安卓程序的webview访问页面 | GET |
| http://192.168.1.103:8001/face/search_face | 人脸识别 | POST |
| http://192.168.1.103:8001/face/add_face | 添加人脸 | POST
| http://192.168.1.103:8001/face/get_faces_info | 获取人脸库信息 | GET
| http://192.168.1.103:8001/face/deleteUser | 删除人脸 | GET

---

**http://192.168.1.103:8001/face/**

- 参数：无
- 返回: webview的html页面

---

**http://192.168.1.103:8001/face/search_face**

- 参数: {image: string}
- 参数说明: 图片的base64编码
- 返回: [{ face_token: String, Location: {left: double, top: double, width: double, height: double, rotation: double}, user_list: [{group_id: String, user_id: String, user_info: String, score" int}]}]

---

**http://192.168.1.103:8001/face/add_face**

- 参数: {userid: String, userinfo: String, user_face: File}
- 返回值: {data: json}
- 返回值说明: 添加成功的提示信息以及服务端存储的内容

---

**http://192.168.1.103:8001/face/get_faces_info**

- 参数: 无
- 返回值: [{userinfo: String, userid: String, urls:[String]}]
- 数据库中的人脸信息


---
**http://192.168.1.103:8001/face/deleteUser**

- 参数: {userid: String}
- 参数说明:要删除的人脸id
- 返回: {error_msg: String}
- 返回说明: 成功或者失败

#### 软件安装及使用说明

##### 软件安装
1. 通过USB来安装

2. 通过releas安装
##### 使用说明

###### 选择

打开本机相册，只能选择一张照片进行操作

###### 拍照

打开本机相机，进行拍照，照片不会存在本地

###### 识别

将选择/拍照 的照片进行识别，并显示识别结果

###### 添加到人脸库

将选择/拍照的照片添加到人脸库和服务器数据库中

###### 底部导航栏

###### 1. Home 
切换到主页面

###### 2. Faces
切换到用户信息管理页面
