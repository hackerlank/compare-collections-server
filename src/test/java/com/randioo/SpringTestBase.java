package com.randioo;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * 利用spring加载测试基类
 */
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration("classpath:ApplicationContext.xml")   //加载配置文件
public class SpringTestBase {

}
