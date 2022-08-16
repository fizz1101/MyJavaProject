package com.fizz.smgp.demo ;

import com.huawei.smproxy.comm.smgp.message.* ;
import com.huawei.smproxy.util.Args ;
import com.huawei.smproxy.util.Cfg ;

/**
 * SMGP协议测试代码
 */

public class SMGPSMProxyDemo
{

	public SMGPSMProxyDemo ()
	{
	}

	public static void main ( String[] args )
	{

		try
		{

//			Args cfgArgs = new Cfg ( "config.xml" ).getArgs ( "SMGPConnect" ) ;
			Args cfgArgs = Env.getConfig().getArgs("SMGPConnect");
			//可以在程序中重新指定配置参数的值，也可以就使用配置文件中的值
			//cfgArgs.set("clientid","sp");
			//cfgArgs.set("shared-secret","new");
			MySMGPSMProxy mySMProxy = new MySMGPSMProxy ( cfgArgs ) ;

			//构造submit消息
			String[] rcvMobile = new String[2] ;
			rcvMobile[0] = "17799147264" ;
			if (args.length > 0) {
				rcvMobile[1] = args[1];
			}

			SMGPSubmitMessage msg = new SMGPSubmitMessage (
					9 ,
					1 ,
					9 ,
					"goodnews13" ,
					"01" ,
					"999" ,
					"" ,
					8,
//				new java.util.Date ( System.currentTimeMillis ()
//				+ 2 * 24 * 60 * 60 * 1000 ) ,
//				new java.util.Date ( System.currentTimeMillis ()
//				+ 2 * 24 * 60 * 60 * 1000 ) ,
					"060429101215032+",
					"060429201215032+",
					"13912345678" ,
					"" ,
					rcvMobile ,
					"this isa test >>> 尊敬的尾号为7264的用户，您的尾号3813的手机可以参与“翼”起转出惊喜来活动，赢取翼支付权益金！关注“乌鲁木齐电信”公众号，发送“红包”即可。" ,
					"0123" ) ;
			//发送消息
			SMGPSubmitRespMessage respMsg = ( SMGPSubmitRespMessage ) mySMProxy.
					send ( msg ) ;
			if ( respMsg != null )
			{
				System.out.println (
						"Get SubmitResp Message Success! The status = "
								+ respMsg.getStatus () ) ;
			}
			else
			{
				System.out.println ( "Get SubmitResp Message Fail!" ) ;
			}
			//--------查询消息----------

			//--------转发消息----------

			//--------MO Route Update Message----------

			//--------MT Route Update Message----------

			try
			{
				Thread.sleep ( 600000 ) ;
			}
			catch ( InterruptedException ie )
			{
				ie.toString () ;
			}

			//关闭连接
			mySMProxy.close () ;
		}
		catch ( Exception e )
		{
			e.printStackTrace () ;
		}
	}

}
