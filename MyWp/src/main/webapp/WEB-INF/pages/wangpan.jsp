<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title></title>
		<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet"/>
		<link rel="stylesheet" type="text/css" href="chat/css/chat.min.css"/>
		<link rel="stylesheet" type="text/css" href="css/wp.css"/>
	</head>
	<style type="text/css">
		
	</style>
	<body>
		<nav class="navbar navbar-default " role="navigation">
			<div class="navbar-header col-sm-2 col-xs-12">
				<a class="navbar-brand" href="">小胖网盘</a>
			</div>
			<div class="navdiv col-sm-7">	
				<form class="col-sm-3">
					<select class="form-control checktype">
					  <option value="all">全部文件</option>
					  <option value="pic">图片</option>
					  <option value="doc">文档</option>
					  <option value="mv">视频</option>
					  <option value="music">音乐</option>
					  <option value="recy">回收箱</option>
					</select>
				</form>
			</div>
			 
			<div class="col-sm-2 col-xs-12">
				<ul class="nav navbar-nav nav2">
					<li>
						<img src='${user["headimgPath"]}' class="img-circle" >
						 
					</li>
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
		 
		 
		<div id="contain" class="panel panel-info col-sm-10 col-sm-offset-1 col-xs-12">
			<div class="panel-heading">
				<div class="fileoperate">
					<div class="col-sm-9 col-xs-12">
						 <input type="file" id="file" style="display: none;" multiple="multiple"  />
						 <input type="file" id="folder" multiple="multiple"  webkitdirectory="" directory style="display: none;"/>
					     <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">上传文件
							<span class="caret"></span>
					     </button>
						 <ul class="dropdown-menu" role="menu">
							<li>
								<a href="javascript:void(0)" id="uploadFile">上传文件</a>
								<a href="javascript:void(0)" id="uploadFolder">上传文件夹</a>
							</li>
						 </ul>
						 <button type="button" data-toggle="modal" data-target="#newFolderModel"  class="btn btn-default ">新建文件夹</button> 
						 <button type="button" class="btn btn-success workbtn" data-toggle="modal" data-target="#uploadwodkModel">任务列表 <span class="badge ">0</span></button>
					</div>
							
					<div class="col-sm-3 col-xs-12">
						<div class="input-group">
							<input type="text" class="form-control" placeholder="搜索文件">
							 <span class="input-group-btn">
					            <button class="btn btn-default seach" type="button"><span class="glyphicon glyphicon-search"></span></button>
					         </span>
						</div>
					</div>
				</div>
				<div class="filepath col-sm-12">
					<ul class="breadcrumb col-sm-9">
						<li class="active" >DESKTOP</li>
					</ul>
					<label class="col-sm-3">
						<center> 已加载:12个文件</center>
					</label>
				</div>
				<div class="filechoose">
					<input type="checkbox">全选
				</div>	
			</div>
			<div class="panel-body">
				
			</div>
			
			<!-- 文件模块的模板 -->
			<div class="fileitemtemple fileitem">
				<div class="fileimg">
					<svg class="icon" aria-hidden="true">
					  <use xlink:href="#icon-wenjianjia"></use>
					</svg>
				</div>
				<div class="filename" title="css">
					css
				</div>
				<div class="filectl">
					<button class="btn btn-primary share" data-toggle="modal" data-target="#shareFileModel" >分享</button>
					<button class="btn btn-warning mesg">详细</button>
					<button class="btn btn-success down">下载</button>
					<button class="btn btn-danger  delete">删除</button>
				</div>
			</div>
		</div>
		
		<!-- 悬浮在右边的聊天logo图标 可自定义  类名不修改 -->
		<div id="chatlogo">
			<span class="iconfont" >&#xe614;</span>
		</div>
	 
		<!-- 文件分享的模态框 -->
		<div class="modal fade   col-xs-12" id="shareFileModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content  col-sm-10">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title">文件分享</h4>
					</div>
					<div class="modal-body">
						<div class="form-group">
							<label >文件名：</label>
							<label  class="filename"></label>
						</div>
						<div class="form-group">
							<label for="name">有效期</label>
							<select class="form-control">
							  <option value="one">1天</option>
							  <option value="seven">7天</option>
							  <option value="always">无限期</option>
							</select>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						<button type="button"  id="shareFile"  class="btn btn-primary">分享</button>
					</div>
				</div> 
			</div> 
		</div>
		<!-- 文件上传的任务栏 -->
		<div class="modal fade   col-xs-12" id="uploadwodkModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content  col-sm-10">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title">正在上传</h4>
					</div>
					<div class="modal-body">
						<ul>
							<li class="hidden">
								<span class="workname">任务名</span>
								<div class="progress progress-striped">
									<div class="progress-bar progress-bar-info " role="progressbar"
										 aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"
										 style="width: 90%;"> 
									</div>
								</div>
							</li>
						</ul>
					</div>
				</div> 
			</div> 
		</div>
		<!-- 新建文件夹模态框 -->
		<div class="modal fade col-xs-12" id="newFolderModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title">请输入文件夹名称</h4>
					</div>
					<div class="modal-body">
						 <input type="text" class="form-control" id="foldername">
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						<button type="button"  id="newFolder"  class="btn btn-primary">新建</button>
					</div>
				</div> 
			</div> 
		</div>
		<script src="icon/iconfont.js"></script>
		<script src="js/jquery.min.js"></script>
		<script src="js/spark-md5.js" type="text/javascript" charset="utf-8"></script>
		<script src="js/angular.min.js" type="text/javascript" charset="utf-8"></script>
		<script src="bootstrap/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="js/wp.js">  </script>
		<script src="chat/js/chat.min.js" type="text/javascript" charset="utf-8"></script>
		<script>
			var chat = new Chat({
				url:"loginchat.action",
				sourcepath:''
			});
			$(function(){
				window.onload = getfiles('/');
			});
		</script>
	</body>
</html>
