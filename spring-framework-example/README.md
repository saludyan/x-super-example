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

围绕这几个问题,我们逐一分析

 - 到底`spring`再后面处理什么?
 - `xml`的信息加载到什么地方
 - `bean`到底存在哪里?
 - `ApplicationContext`是怎么获取`xml`中`bean`的定义?

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

> `Environment` n.环境，外界;周围，围绕;工作平台;（运行）环境
            
> `Capable` adj.能干的;有才能的;有才华的;能胜任的
           
叫他`环境能力`比较奇怪,这里叫`提供环境`吧

 -  `getEnvironment()` : 返回与此组件关联的环境

里面只有一个`getEnvironment()`获取环境的方法,顾名思义它的作用就是提供环境变量的


###### ListableBeanFactory

> `Listable` 列表, adj. 能列在单子上的

按名称来解释就是,可列出`Bean`的工厂,提供容器中bean迭代的功能,不再需要一个个bean地查找.如下:

- `containsBeanDefinition()` : 检查此bean工厂是否包含具有给定名称的bean定义
- `getBeanDefinitionCount()` : 返回工厂中定义的bean数目。
- `getBeanDefinitionNames()` : 返回此工厂中定义的所有bean的名称。
- `getBeanNamesForType(ResolvableType)` : 根据bean定义或`ResolvableType.getObjectType`的值，返回与给定类型（包括子类）匹配的bean的名称。
- `getBeanNamesForType(Class)` : 返回与给定类型（包括子类）匹配的bean名称，根据bean定义或`factorybeans`中`getObjectType`的值进行判断
- `getBeanNamesForType(Class,includeNonSingletons:boolean , allowEagerInit:boolean )` 

   - `includeNonSingletons` : 是否也包括原型或作用域bean，或者只包括单例bean（也适用于`Factorybean`）
   - `allowEagerInit` : 
   
是否初始化`FactoryBeans`（或通过带有“factory bean”引用的工厂方法）创建的lazy init单例和对象进行类型检查。请注意，`FactoryBeans`需要预先初始化以确定其类型：因此请注意，为此标志传入“true”将初始化factorybeans和“factory bean”引用。
- `getBeansOfType(Class)`有类似3个方法,这里不一一解释了 : 返回与给定对象类型（包括子类）匹配的bean实例，根据bean定义或`FactoryBeans`的`getObjectType`值判断
- `getBeanNamesForAnnotation()` 通过注解获取BeanName集
- `getBeansWithAnnotation()` 和`getBeanByType`差不多,这个方法是通过`注解@`
- `findAnnotationOnBean()` 通过BeanName和注解获取,大同小异

小结一下`ListableBeanFactory`提供了如下方法:

- 通过`BeanName`判断是否定义了
- 统计已定义`Bean`的个数
- 获取已定义的`Bean`集合
- 可通过`Type`获取`BeanNames`,`Bean`集合(记住是`Map`)
- 也可通过`@Annotation`获取`BeanNames`,`Bean`集合


`ListableBeanFactory` 继承 `BeanFactory` 接口,下面来分析一下主要方法:

- `getBean()` : 返回指定bean的实例，该实例可以是共享的，也可以是独立的。

    - `getBean` 有多种获取方法,这里不一一列举
    
- `getBeanProvider()` : 返回指定bean的提供程序，允许延迟按需检索(TODO 直接翻译的,以后再弄明白)
- `isSingleton()` : 是否单例 - 这是单例共享的bean吗？也就是说，将始终返回相同的实例？
- `isPrototype()` : 是否原型 - 这个bean是原型吗？也就是说，总是返回独立的实例吗？
- `isTypeMatch(String,ResolvableType)` : 检查具有给定名称的bean是否与指定类型匹配。
- `isTypeMatch(String,Class)` : 同上
- `getType(String)` : 这类型的确定与给定名称的bean。更具体来说，确定的类型的对象，将返回给定的名称
- `getAliases(String)` : 返回给定bean名称的别名（如果有）。

`Simple`代码中,`getBean('animal')`就是调用该接口,具体实现后面会具体分析

###### HierarchicalBeanFactory

> `Hierarchical` adj.分层的;按等级划分的，等级（制度）的

跟`ListableBeanFactory`对比,这个接口具有层级关系的`BeanFactory`,同样它也是继承`BeanFactory`,
这里可以猜到`spring`的命名规则,只要继承`BeanFactory`接口,都叫`XxxxBeanFactory`

`HierarchicalBeanFactory`只有两个方法

- `getParentBeanFactory()` : 返回父bean工厂，如果没有则返回null。
- `containsLocalBean(String)` : 返回本地bean工厂是否包含给定名称的bean，忽略祖先上下文中定义的bean。

###### MessageSource

用于解析消息的策略接口，支持此类消息的参数化和国际化

- `getMessage()` 多参数获取消息,一般用于获取已定义的消息(国际化),尝试解决此消息。如果找不到消息，则返回默认消息。

###### ApplicationEventPublisher

- `publishEvent(ApplicationEvent)` : 将应用程序事件通知所有与此应用程序注册的匹配侦听器。事件可以是框架事件（如RequestHandledEvent）或特定于应用程序的事件。

`ApplicationEvent`最终也会转成`Object`,看下面代码

```java
    default void publishEvent(ApplicationEvent event) {
		publishEvent((Object) event);
	}
```

这里注意的是`ApplicationEventPublisher`有个`@FunctionalInterface`注解,该注解解释如下




```text
1、该注解只能标记在”有且仅有一个抽象方法”的接口上。 
2、JDK8接口中的静态方法和默认方法，都不算是抽象方法。 
3、接口默认继承java.lang.Object，所以如果接口显示声明覆盖了Object中方法，那么也不算抽象方法。 
4、该注解不是必须的，如果一个接口符合”函数式接口”定义，那么加不加该注解都没有影响。加上该注解能够更好地让编译器进行检查。如果编写的不是函数式接口，但是加上了@FunctionInterface，那么编译器会报错。
```

(这里暂时不详细说明该接口,往后分析中会结合到一起解说)


###### ResourcePatternResolver

> `Resource` n.	资源; 物力，财力; 办法; 智谋;

> `Pattern` n.	模式; 图案; 花样，样品; 榜样，典范;

> `Resolver` n.	下决心者，解决[答]问题者; 溶媒; 求解仪;

感觉叫他`资源模式分析器`比较好听

源码注释翻译如下:用于将位置模式（例如，Ant样式的路径模式）解析为资源对象的策略接口。

该接口只有一个方法

- `getResources(String): Resource[]`将给定的位置模式解析为资源对象。返回可能是多个`Resource`

该接口继承`ResourceLoader`,方法如下

- `getResources()` 跟`ResourcePatternResolver`不同,它只返回单个`Resource`
- `getClassLoader():ClassLoader` : 公开此资源加载器使用的类加载器

> 好吧,看了那么多接口和方法,直接看依赖图吧

ApplicationContext依赖如下:

![ApplicationContext](uml/ApplicationContext.png)

##### 具体实现 `ClassPathXmlApplicationContext`