#coding:utf-8
from email import encoders
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from email.mime.base import MIMEBase
from datetime import datetime

import smtplib
import zipfile
import glob
import os

today = datetime.now()

#Database Configs.
host = '93.188.161.199'
port = '9876'
password = 'WyDadaVaX'
user = 'root'
database = 'unichat'
filename_database = '{0}-backup-{1}-{2}-{3}.dump'.format(database, today.day, today.month, today.year)

#Zip file configs
path_backup_files = '/var/www/*'
zip_filename = 'backup-{0}-{1}-{2}.zip'.format(today.day, today.month, today.year)

def backup_database():
    print 'Executando comando de backup...'
    backup_command = 'mysqldump -h {0} -P {1} -u {2} -p{3} {4} > {5}'.format(host, port, user, password, database, filename_database)
    os.system(backup_command)
    print 'Backup completo.'

def bacup_files():
    print 'Executando backup dos arquivos...'
    with zipfile.ZipFile(zip_filename, 'w') as zip_file:
        for item in glob.glob(path_backup_files):
            with open(item, 'r') as file_to_backup:
                zip_file.writestr(item, file_to_backup.read())
        with open(filename_database, 'r') as dump_file:
            zip_file.writestr(filename_database, dump_file.read())

backup_database()
bacup_files()
    
fromaddr = 'thiago.silva@ifsudestemg.edu.br'
toaddrs  = ['thiagodd.silva@gmail.com', 'joao.menighin@gmail.com']
username = 'thiago.silva@ifsudestemg.edu.br'  
password = 'barbacena'  

print 'Enviando Email.'
server = smtplib.SMTP('smtp.gmail.com:587')  
server.starttls()  
server.login(username,password)  

#Create a message.
message = MIMEMultipart('alternative')
message['subject'] = 'Backup automático do Banco de Dados'

#Load zip
f = file(zip_filename)

#Create zip from send
attachment = MIMEBase('application', 'zip')
attachment.set_payload(f.read())

#Encode zip in base64
encoders.encode_base64(attachment)
attachment.add_header('Content-Disposition', 'attachment', filename=zip_filename)

#Attache zip
message.attach(attachment)

server.sendmail(fromaddr, toaddrs, message.as_string())  
server.quit()  
print 'Email Enviado.'
os.remove(zip_filename)
os.remove(filename_database)