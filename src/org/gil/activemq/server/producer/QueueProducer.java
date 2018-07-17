package org.gil.activemq.server.producer;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.gil.activemq.server.model.JianRen;  
  
//Queue（点对点）方式  生存者Producer  
public class QueueProducer {
    private static String user = ActiveMQConnection.DEFAULT_USER;  
    private static String password =ActiveMQConnection.DEFAULT_PASSWORD;  
//    private static String user ="jianbo";
//    private static String password ="jianbo";
    private static String url =  "tcp://localhost:61616";  
  
    public static void main(String[] args)throws Exception {  
         // ConnectionFactory ：连接工厂，JMS 用它创建连接  
    	ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        // Connection ：JMS 客户端到JMS Provider 的连接  
        //当一个程序执行完成后，必须关闭之前创建的Connection，否则ActiveMQ不能释放资源，关闭一个Connection同样也关闭了Session，MessageProducer和MessageConsumer。
        Connection connection = connectionFactory.createConnection();
        // Connection 启动 
    	connection.start();
        System.out.println("Connection is start...");  
        // Session： 一个发送或接收消息的线程  
        /**
         * 一旦从ConnectionFactory中获得一个Connection，
         * 就必须从Connection中创建一个或者多个Session。Session是一个发送或接收消息的线程，
         * 可以使用Session创建MessageProducer，MessageConsumer和Message。
         */
       Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // Queue ：消息的目的地;消息发送给谁.
        /**
         * Destination是一个客户端用来指定生产消息目标和消费消息来源的对象。
         * 在PTP模式中，Destination被称作Queue即队列；
         * 在Pub/Sub模式，Destination被称作Topic即主题。
         * 在程序中可以使用多个Queue和Topic
         */
        Topic queue = session.createTopic("example.A");
        // MessageProducer：消息发送者;是一个由Session创建的对象，用来向Destination发送消息。
        MessageProducer producer = session.createProducer(queue);
        //为所有消息设置传送模式:NON_PERSISTENT（非持久性消息）,PERSISTENT（持久性消息(默认)）
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        //消息优先级从0-9十个级别，0-4是普通消息，5-9是加急消息。如果不指定优先级，则默认为4。
        //JMS不要求严格按照这十个优先级发送消息，但必须保证加急消息要先于普通消息到达。
//        producer.setPriority(4);
        //默认情况下，消息永不会过期，即为0。时间单位为毫秒.
//        producer.setTimeToLive(10000);
         // 构造消息，此处写死，项目就是参数，或者方法获取 
        sendMessage(session, producer);
        //事务有这个
//        session.commit();
        
        connection.close();
        System.out.println("send text ok.");
    }
      
    public static void sendMessage(Session session, MessageProducer producer)  
            throws Exception {  
        /*for (int i = 1; i <= 10; i++) {
            TextMessage message = session.createTextMessage("jianbo的ActiveMq 发送的消息" + i+i);
            message.setStringProperty("from", "梁海军");
            // 发送消息到目的地方  
            System.out.println("发送消息：" + "ActiveMq 发送的消息" + i);
            producer.send(message);
        } */ 
        JianRen jianRen = new JianRen("梁海军","卑鄙，无耻，下流，淫荡，风骚，贱！");
        ObjectMessage mes=session.createObjectMessage(jianRen);
        producer.send(mes);
    }  
    
    
}  
