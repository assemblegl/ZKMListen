package gl.mail;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;

public class Mail {
	private String SMTPHost;
	private String MailLang;
	private String SendUserMail;
	private String SendUserName;
	private String SendUserPass;
	private String[] toMail;
	
	public void htmlmail(String subject,String msg){
		htmlmail(toMail,subject,msg);
	}
	
	public void htmlmail(String toAddress,String subject,String msg){
		String[] tmptoAddress = new String[1];
		tmptoAddress[0]=toAddress;
		htmlmail(tmptoAddress,subject,msg,null);
	}
	
	public void htmlmail(String[] toAddress,String subject,String msg){		
		htmlmail(toAddress,subject,msg,null);
	}
	
	public void htmlmail(String toAddress,String subject,String msg,String[] filelist){
		String[] tmptoAddress = new String[1];
		tmptoAddress[0]=toAddress;
		htmlmail(tmptoAddress,subject,msg,filelist);
	}
	
	public void htmlmail(String[] toAddress,String subject,String msg,String[] filelist){
		try {
			HtmlEmail email = new HtmlEmail();
		    // 这里是SMTP发送服务器的名字：，163的如下：
		    email.setHostName(SMTPHost);
		    // 字符编码集的设置
		    email.setCharset(MailLang);
		    // 收件人的邮箱
		    
		    int addnum=toAddress.length;
        	//System.out.println("sqlnum"+sqlnum);
        	for(int i=0;i<addnum;i++){
        		//System.out.println("sql[i]"+sql[i]);
        		if(toAddress[i]!=null && (!toAddress[i].isEmpty())){
        			email.addTo(toAddress[i]);
        		}       		
        	}
		    		    
		    // 发送人的邮箱
		    email.setFrom(SendUserMail,"report system");
		    // 如果需要认证信息的话，设置认证：用户名-密码。分别为发件人在邮件服务器上的注册名称和密码
		    email.setAuthentication(SendUserName,SendUserPass);
		    email.setSubject(subject);
		    // 要发送的信息，由于使用了HtmlEmail，可以在邮件内容中使用HTML标签
		    email.setMsg(msg);
		    
		  //发送附件
		    if(filelist != null){
			    int attnum = filelist.length;
			    for(int i=0;i<attnum;i++){       		
	        		if(filelist[i]!=null && (!filelist[i].isEmpty())){
	        			EmailAttachment attachment = new EmailAttachment();
	        		    attachment.setPath(filelist[i]);
	        		    System.out.println("file:"+filelist[i]);
	        		    attachment.setDisposition(EmailAttachment.ATTACHMENT);
	        		    email.attach(attachment);
	        		}       		
	        	}
		    }		   	   
		    
		    // 发送
		    email.send();		   
		    System.out.println("ok .Send email to:"+toAddress+",subject:"+subject);
		   }catch (Exception e) {
		    e.printStackTrace();
		    System.out.println("error in html mail:"+e.getMessage());
		   }
	}

	public String getSMTPHost() {
		return SMTPHost;
	}

	public void setSMTPHost(String sMTPHost) {
		SMTPHost = sMTPHost;
	}

	public String getMailLang() {
		return MailLang;
	}

	public void setMailLang(String mailLang) {
		MailLang = mailLang;
	}

	public String getSendUserMail() {
		return SendUserMail;
	}

	public void setSendUserMail(String sendUserMail) {
		SendUserMail = sendUserMail;
	}

	public String getSendUserName() {
		return SendUserName;
	}

	public void setSendUserName(String sendUserName) {
		SendUserName = sendUserName;
	}

	public String getSendUserPass() {
		return SendUserPass;
	}

	public void setSendUserPass(String sendUserPass) {
		SendUserPass = sendUserPass;
	}

	public String[] getToMail() {
		return toMail;
	}

	public void setToMail(String[] toMail) {
		this.toMail = toMail;
	}

	
	

}
