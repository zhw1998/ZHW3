<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>文件分享</title>
		<link href="../bootstrap/css/bootstrap.min.css" rel="stylesheet"/>
		<link rel="stylesheet" type="text/css" href="../css/wp.css"/>
	</head>
	<style>
		 #contain{height: 350px;}
		 #contain .panel-body{height: 300px;}
		 .fileitem{float: none; margin:20px auto;}
	</style>
	<body>
		
		<nav class="navbar navbar-default " role="navigation">
			<div class="navbar-header col-sm-2 col-xs-12">
				<a class="navbar-brand" href="../disk">小胖网盘</a>
			</div>
				
			 <div class="navbar-header col-sm-8 col-xs-12">
			 	 
			 </div>
			<div class="col-sm-2 col-xs-12">
				<ul class="nav navbar-nav nav2">
					<li><img src="${user["headimgPath"]}" class="img-circle" ></li>
					<li class="dropdown">
						<a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown">
							${user["username"]}
							<b class="caret"></b>
						</a>
						<ul class="dropdown-menu">
							<li><a href="user/logout.action">退出</a></li>
						</ul>	
					</li>
				</ul>
			</div>
			</div>
		</nav>
		
		<div id="contain" class="panel panel-info col-sm-6 col-sm-offset-3 col-xs-12">
			<div class="panel-heading">
				 文件分享
			</div>
			<div class="panel-body" >
				<div class="fileitem"  data-id='${userfiles["id"]}' 
					data-type="file" file-size='${userfiles["fileSize"]}' file-time='${userfiles["updateTime"]}'>
					<div class="fileimg">
						<svg class="icon" aria-hidden="true" title='${userfiles["fileName"]}'
						data-placement="right" data-container="body" data-toggle="popover" data-html="true"
						data-content="" >
						  <use xlink:href="#icon-wenjianjia"></use>
						</svg>
					</div>
					<div class="filename" title="css">
						${userfiles["fileName"]}
					</div>
					<div class="filectl">
						<button class="btn btn-warning mesg">详细</button>
						<button class="btn btn-success" data-toggle="modal" data-target="#codeModel">下载</button>
					</div>
				</div>
			</div>
			
		</div>
		
		<!-- 输入提取码的模态框 -->
		<div class="modal fade col-xs-12" data-id='${shareFile["id"]}' id="codeModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title">请输入文件夹名称</h4>
					</div>
					<div class="modal-body">
						 <input type="text" class="form-control" id="sharecode">
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						<button type="button"  id="sharedown"  class="btn btn-primary">确定</button>
					</div>
				</div> 
			</div> 
		</div>
		<script src="../icon/iconfont.js"></script>
		<script src="../js/jquery.min.js"></script>
		<script src="../bootstrap/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../js/wp.js">  </script>
		<script>
		 
				window.onload = function(){
					setsharafile();
				};
				function setsharafile(){
					var fileitem = $('#contain .fileitem');
					if(fileitem.attr('data-id')==''){
						fileitem.find('.filename').text('文件已失效或已被删除！');
						fileitem.find('.fileimg use').attr('xlink:href',"#icon-file_unknown");
						//删fileitem。除按钮
						fileitem.find('.filectl').remove();
						return;
					}
					var fsvg = geticonbyfilename(fileitem.find('.filename').text().trim());
					var filesize = parseInt(fileitem.attr('file-size'))
					//根据文件名获取响应图标
					fileitem.find('.fileimg use').attr('xlink:href',fsvg)
					var size = filesize/1024/1024;
					if(size<1){
						size = String(Math.ceil(size*1024)).concat('KB');
					}else if(size>1024){
						size = String(Math.ceil(size/1024)).concat('G');
					}else{
						size = String(Math.ceil(size)).concat('M');
					}
					var content ='<p>大小: '+size+'</p>'+'<p>时间: '+fileitem.attr('file-time').slice(0,16)+'</p>';
					fileitem.find('svg').attr('data-content',content);
				}
				
				//文件下载
				$('#sharedown').click(function(){
					//获取输入的code
					var code = $('#sharecode').val().trim();
					 
					if(code == ''){
						$('#sharecode').focus();
						return;
					}
					//获取分享文件的id
					var shareid = $('#codeModel').attr('data-id');
					//发送该文件分享请求
					window.open('downshare.action?shareid='+shareid+'&downcode='+code,'_blank');      
				});
				
		</script>
	</body>
</html>
