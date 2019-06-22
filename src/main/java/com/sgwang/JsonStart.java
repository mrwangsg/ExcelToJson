package com.sgwang;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @创建人 sgwang
 * @name JsonStart
 * @user 91119
 * @创建时间 2019/6/21
 * @描述
 */
public class JsonStart {
    private Map<String, String> tempData;

    public static void main(String[] args) throws Exception {
        String jsonStr = new JsonStart().doStart(TestData.getTestMapData());
        System.out.println(jsonStr);
    }

    public String doStart(Map<String, String> initMap){
        this.tempData = initMap;
        Set<String> initMapOfSet = initMap.keySet();

        Node rootNode = new Node("root", "obj");
        JSONObject rootObjJson = new JSONObject();
        handlerTask(initMapOfSet, rootNode, rootObjJson);

        return rootObjJson.toJSONString();
    }

    /**
     * @描述 将字符串 转化为 树型结构
     * @参数 String[] initStr
     * @返回值 void
     */
    public void handlerTask(Set<String> initStr, Node rootNode, JSONObject rootObjJson) {

        // 模拟循环 excel的列
        for (String indexStr : initStr) {
            // 切割字符串时 需要递归一下
            buildNodeTree(indexStr, indexStr, rootNode);
        }

        levelIteratorTree(rootNode, rootObjJson, null);
    }

    /**
     * @描述 输入当前Node 和 当前切割的字符串
     * @参数 Node indexNode && String indexStr
     * @返回值 void
     */
    public void buildNodeTree(String indexStr, String keyStr, Node indexNode) {

        // 存在"." || "]" 说明类型为 obj || arr
        if (indexStr.contains(".") || indexStr.contains("]")) {
            String indexNodeName = "";                  // 当前节点 名称
            String indexNodeType = diffType(indexStr);  // 区别节点 类型
            Node nextNode = null;                       // 下一child节点
            String nextStr = "";                        // 下一child字符串

            if (indexNodeType == "arr") {
                indexNodeName = indexStr.substring(0, indexStr.indexOf("["));

                // 可能是基本类型数组 即不存在"."
                if (indexStr.contains(".")) {
                    nextStr = indexStr.substring(indexStr.indexOf(".") + 1);

                    // 这一步 是为了实现 数组对对象 一对多
                    String arrayNumberMarker = "$" + indexStr.substring(indexStr.indexOf("[") + 1, indexStr.indexOf("]")) + "$";
                    nextStr = arrayNumberMarker + nextStr;
                }
            } else if (indexNodeType == "obj") {
                indexNodeName = indexStr.substring(0, indexStr.indexOf("."));
                nextStr = indexStr.substring(indexStr.indexOf(".") + 1);
            }

            // 如果已经存在孩子节点 获取它 递归子节点
            // 如果不存在 先新建Node节点插入 再递归子节点
            if (indexNode.isExistChildNode(indexNodeName)) {
                nextNode = indexNode.getChildNodeName(indexNodeName);
            } else {
                nextNode = new Node(indexNodeName, indexNodeType, keyStr);
                indexNode.addChildNode(nextNode);
            }

            if (indexNodeType == "arr") {
                // 用于记录 特别arr的 数组对象个数
                signIndexArrSize(nextNode, indexStr);
            }

            buildNodeTree(nextStr, keyStr, nextNode);
        } else {
            // 不存在"." 说明已经是尽头
            if (indexNode.isExistChildNode(indexStr)) {
                // 如果已经存在不做处理
            } else {
                // 如果不存在 新建Node节点 插入
                indexNode.addChildNode(new Node(indexStr, "attr", keyStr));
            }
        }

    }

    /**
     * @描述 层级遍历树结构
     * @参数 Node indexNode
     * @返回值 void
     */
    public void levelIteratorTree(Node indexNode, JSONObject indexJsonObj, JSONArray indexJsonArr) {
        //  这里可以做构建 json的处理
        List<Node> childrenNode = indexNode.getChildrenNode();
        if (!childrenNode.isEmpty()) {
            Iterator<Node> iterator = childrenNode.iterator();
            while (iterator.hasNext()) {
                Node childNode = iterator.next();
                String childNameNode = childNode.getNameNode();
                String childTypeNode = childNode.getTypeNode();

                if (indexJsonObj != null) {
                    if (childTypeNode == "attr") {
                        String keyStr = childNode.getKeyStr();

                        indexJsonObj.put(childNameNode, this.tempData.get(keyStr));
                    } else if (childTypeNode == "obj") {
                        JSONObject nextObjJson = new JSONObject();

                        indexJsonObj.put(childNameNode, nextObjJson);
                        levelIteratorTree(childNode, nextObjJson, null);
                    } else if (childTypeNode == "arr") {
                        JSONArray nextArrJson = new JSONArray();

                        indexJsonObj.put(childNameNode, nextArrJson);
                        levelIteratorTree(childNode, null, nextArrJson);
                    }
                } else if (indexJsonArr != null) {

                    if (childTypeNode == "attr") {
                        // 因为是array下 所以循环
                        Iterator<String> iteratorInner = indexNode.getArrSize().iterator();
                        while (iteratorInner.hasNext()) {
                            // 这里是为了获取数组序号标记 其实主要还是消费完迭代器
                            String arrayNumberMarker = iteratorInner.next();
                            String keyStr = childNode.getKeyStr();

                            indexJsonArr.add(this.tempData.get(keyStr));
                        }
                    } else if (childTypeNode == "obj") {
                        JSONObject nextObjJson = new JSONObject();

                        indexJsonArr.add(nextObjJson);
                        levelIteratorTree(childNode, nextObjJson, null);

                    } else if (childTypeNode == "arr") {
                        JSONArray nextArrJson = new JSONArray();

                        indexJsonArr.add(nextArrJson);
                        levelIteratorTree(childNode, null, nextArrJson);
                    }
                }
            }
        }
    }

    /**
     * @描述 根据字符串 返回arr | obj | attr
     * @参数 String
     * @返回值 String 备注：arr | obj | attr
     */
    public String diffType(String indexStr) {

        if (indexStr.contains(".") && indexStr.contains("]")) {
            if (indexStr.indexOf(".") < indexStr.indexOf("]")) {
                return "obj";
            } else {
                return "arr";
            }
        } else if (indexStr.contains(".")) {
            return "obj";
        } else if (indexStr.contains("]")) {
            return "arr";
        } else {
            return "attr";
        }
    }

    /**
     * @描述 用于记录 特别arr的节点 数组对象个数
     * @参数 Node indexNode, String indexStr
     * @返回值 void
     */
    public void signIndexArrSize(Node indexNode, String indexStr) {
        String signSize = indexStr.substring(indexStr.indexOf("["), indexStr.indexOf("]") + 1);
        Set<String> ret = indexNode.getArrSize();
        ret.add(signSize);
    }
}
