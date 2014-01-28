#coding:utf-8
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from datetime import datetime

import smtplib
import os

today = datetime.now()
host = '93.188.161.199'
port = '9876'
password = 'WyDadaVaX'
user = 'root'
database = 'unichat'
filename = '{0}-backup-{1}-{2}-{3}.dump'.format(database, today.day, today.month, today.year)

def backup_database():
    print 'Executando comando de backup...'
    backup_command = 'mysqldump -h {0} -P {1} -u {2} -p{3} {4} > {5}'.format(host, port, user, password, database, filename)
    os.system(backup_command)
    print 'Backup completo.'
    
backup_database()
    
fromaddr = 'thiago.silva@ifsudestemg.edu.br'
toaddrs  = ['thiagodd.silva@gmail.com', 'joao.menighin@gmail.com']
username = 'thiago.silva@ifsudestemg.edu.br'  
password = 'barbacena'  

print 'Enviando Email.'
server = smtplib.SMTP('smtp.gmail.com:587')  
server.starttls()  
server.login(username,password)  

message = MIMEMultipart('alternative')
message['subject'] = 'Backup automático do Banco de Dados'
f = file(filename)
attachment = MIMEText(f.read())
attachment.add_header('Content-Disposition', 'attachment', filename=filename)
message.attach(attachment)

server.sendmail(fromaddr, toaddrs, message.as_string())  
server.quit()  
print 'Email Enviado.'
os.remove(filename)