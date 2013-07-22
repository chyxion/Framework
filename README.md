Framework
=========

Java Web 框架，JFinal衍生版本，修改程度较大，大量采用JSON格式数据交换，方便与ExtJS整合。
样例：
* [Demo Grid](https://github.com/chyxion/DemoGrid)，基本的Grid展示，分页，查询
* [Demo CRUD](https://github.com/chyxion/DemoCRUD)，增删改查样例

## 配置web.xml
    
	<filter> 
        <filter-name>app</filter-name> 
        <filter-class>com.shs.framework.core.CoreFilter</filter-class> 
    </filter> 
    <filter-mapping> 
        <filter-name>app</filter-name> 
        <url-pattern>/*</url-pattern> 
    </filter-mapping>

## 编写Controller

    @RouteMapping(controller="/")
    public class SiteController extends BaseController {
        public void index() {
            jsp("/index");
        }
    }

License
==================================

* 这是本人工作中积累的一些东西，如果能对这个世界有点作用，就拿去使用吧！
* 许可证， GPL2 
* 有什么需要支持或者帮助或者介绍工作机会请联系 chyxion@163.com

