# spring-framework-example
(整理中...)
> 源码学习系列

## 简单示例 Simple
> 本文使用`lombok`为了更简洁的代码
从最简单的方式加载Bean开始分析,

### 创建个`Animal.java`类

代码如下:
```java
@Data
public class Animal {
    private String name;
    private String category;
    private String color;
}
```

### 创建 `Simple.java` 类
代码如下:
```java
public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("Simple.xml");
        Animal animal = (Animal) context.getBean("animal");
        System.out.println(animal);
}
```

### 创建 `Simple.xml`

主要代码如下:
```xml
<bean name="animal" class="com.x.sp.example.spring.entity.Animal">
    <property name="name" value="小黄"/>
    <property name="category" value="狗"/>
    <property name="color" value="黄色"/>
</bean>
```

### 运行结果

控制台输出:

```text
Animal(name=小黄, category=狗, color=黄色)
```

### 分析

> 这里不说概念,网上一大堆,只分析过程

到底`spring`再后面处理什么

#### `ApplicationContext` 分析


> `application` 通常翻译应用程序,百度翻译如下:n.
                    适用，应用，运用;申请，请求，申请表格;勤勉，用功;敷用，敷用药

> `context` n.
            语境;上下文;背景;环境
           
解释就是`应用程序上下文`,它是`spring`中常用到一个核心类

##### 方法分析

   - `getId()` : 返回此`应用程序上下文`的唯一ID。
   - `getApplicationName()` : 返回此上下文所属的已部署应用程序的名称。
   - `getDisplayName()` : 返回此上下文的友好名称。
   - `getStartupDate()` : 返回首次加载此上下文时的时间戳。
   - `getParent()` : 返回父上下文，如果没有父上下文，并且这是上下文层次结构的根，则返回空。
   - `getAutowireCapableBeanFactory()` : 为此上下文公开AutoWireCapableBeanFactory功能。
   
> `getAutowireCapableBeanFactory()`这通常不被应用程序代码使用，除非是为了初始化不在应用程序上下文中的bean实例，将SpringBean生命周期（全部或部分）应用于它们。
          
##### 继承分析
`ApplicationContext`是一个接口,它继承如下:
 - EnvironmentCapable
 - ListableBeanFactory
 - HierarchicalBeanFactory
 - MessageSource
 - ApplicationEventPublisher
 - ResourcePatternResolver
 
 有点多...,我们逐一分析每个类的用处
 
###### EnvironmentCapable

> `Environment` n.
                 环境，外界;周围，围绕;工作平台;（运行）环境
            
> `Capable` adj.
            能干的;有才能的;有才华的;能胜任的
           
叫他`环境能力`比较奇怪,这里叫`提供环境`吧

里面只有一个`getEnvironment()`获取环境的方法,顾名思义它的作用就是提供环境变量的


###### ListableBeanFactory

> `Listable` 列表, adj. 能列在单子上的

按名称来解释就是,可列出`Bean`的工厂,提供容器中bean迭代的功能,不再需要一个个bean地查找.如下:

- 

ApplicationContext依赖如下:

![ApplicationContext](uml/ApplicationContext.png)
