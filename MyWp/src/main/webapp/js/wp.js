 
		var FIlE_CUT_MAXSIZE = 1024*1024*30  //定义最大切片为30M
			
			var interval = null;
			//上传文件的按钮事件绑定
			$('#uploadFile').click(function(){
				$('#file').click();
				 
			});
			$('#uploadFolder').click(function(){
				$('#folder').click();
			});
			//鼠标经过文件图标的事件
			$('.panel-body').on('mouseenter','.fileitem .fileimg',function(){
				$(this).siblings('.filectl').css({display:'flex'});
			});
			$('.panel-body').on('mouseleave','.fileitem',function(){
				$(this).find("[data-toggle='popover']").popover('hide');
				$(this).find('.filectl').slideUp()
			});
			//对文件操作按钮事件的监听
			$('.panel-body').on('click','.fileitem .filectl button',function(e){
				if(!$(e.target).hasClass('btn')) return;
				//判断是文件夹还是文件
				var fid = '';
				var type = '';
				var fileitem = $(this).parents('.fileitem');
				var fname = fileitem.find('.filename').text();;
				if(fileitem.attr('data-type')=='file'){
					fid = fileitem.attr('data-id');//获取id
					type = 'file';
				}else{//获取文件名
					type = 'folder';
					fname = getcurrentpath().concat(fname,'/');
					if(fname=='') return;
				}
				if($(e.target).hasClass('down')){//下载  文件夹通过路径  文件通过  fid
					downfile(fid);
					// window.open('file/downfile.action?fid='+fid,"_blank");  
				}else if($(e.target).hasClass('share')){//分享
					if(fid == '') return false;  //文件先不做分享
					//删除之前的信息
					$('#shareFileModel .form-group.after').remove();
					//获取fid，文件名 设置模态框
					$('#shareFileModel').attr('data-id',fid);
					$('#shareFileModel .modal-body .filename').text(fname);
				}else if($(e.target).hasClass('mesg')){//详情
					fileitem.find("[data-toggle='popover']").popover('show');
				}else if($(e.target).hasClass('delete')){//删除
					if(confirm('确定删除文件('+fname+')吗！')){
						var data = {
							type:type,
							fid:fid,
							foldname:fname
						}
						$.get('file/deletefile.action',data,function(res){
							console.log(res);
							if(res.code == 200){
								//移除
								fileitem.fadeOut();
							}else{
								alert(res.mesg);
							}
						})
					}
				}
			});
			//监听模态框上的分享按钮点击事件
			$('#shareFile').click(function(){
				//获取文件id 和有效期
				var fid = $('#shareFileModel').attr('data-id');
				if(fid=='') return;
				var time = $('#shareFileModel .modal-body select').val();
				//发送请求 
				$.post('file/share.action',{fid:fid,time:time},function(res){
					console.log(res)
					if(res.code == 200){
						//将链接和提取码实现在模态框
						var templ1 = $('#shareFileModel .modal-body .form-group').eq(0).clone();
						templ1.find('label').eq(0).text('链接：');
						templ1.find('.filename').text(res.mesg);
						templ1.addClass('after');
						$('#shareFileModel .modal-body').append(templ1);
						var templ2 = templ1.clone();
						templ2.find('label').eq(0).text('提取码：');
						templ2.find('.filename').text(res.result.downCode);
						$('#shareFileModel .modal-body').append(templ2);
					}else{
						alert(res.mesg)
					}
				})
				//接收响应 获取下载链接和提取码到模态框
			});
			 
			$('#file').change(function(){
				if($(this).val() == '') return; 
				upload($(this)[0].files);
				//开启进度监听
			    interval = setInterval(function(){
					// console.log('开始')
					uploadspeed();
				},2000);
			})
			$('#folder').change(function(){
				if($(this).val() == '') return; 
				upload($(this)[0].files)
				 interval = setInterval(function(){
					uploadspeed();
				},2000);
			})
			//新建文件夹按钮事件
			$('#newFolder').click( function(){
				var foldername = $('#foldername').val().trim();
				if(foldername == ''){
					$('#foldername').focus();
					return;	
				}
				// 不允许出现/  和  .
				var reg1 = /[\/]+/;
				var reg2 = /[.]+/;
				if(reg1.test(foldername)||reg2.test(foldername)){
					alert('文件夹名称不能有 / . ')
					$('#foldername').focus();
					return;	
				}
				//如果当前目录下已存在该目录就添加其他的
				//获取当前网盘的路径
				var currentpath = getcurrentpath();
				foldername = hadsamepath(foldername);
				var filepath = currentpath.concat(foldername,'/'); //相当于文件夹名
				// console.log(filepath)
				$(this).attr("disabled", true); //设置不可点击
				$('#foldername').val('');
				//发送新建文件夹请求
				$.get('file/newfolder.action',{filepath:filepath},function(res){
					// console.log(res)
					if(res.code==200){
						//手动生成一个文件夹显示  避免请求
						var item = $('#contain .fileitemtemple').clone();
						item.removeClass("fileitemtemple");
						item.find('.filename').attr('title',foldername).text(foldername);
						item.find('button.mesg').remove();
						$('#contain .panel-body').append(item);
					}else{
						alert('文件夹创建失败')
					}
					$('#newFolder').attr("disabled", false); //设置不可点击
				});
			});
			//文件添加点击事件 事件委托
			$('#contain .panel-body').click(function(e){
				var that = e.target;
 				if(that.tagName == 'use'){ //点击了图标 文件夹就打开目录  文件就预览
					var filename = $(that).parents('.fileimg').siblings('.filename').text().trim();
					//判断是否为文件夹
					if($(that).attr('xlink:href') == '#icon-wenjianjia'){
						$('.filepath label center').text('正在加载文件。。。。。。。');
						 var path = getcurrentpath().concat(filename,'/'); //拼接路径
						 gotopath(filename)		//设置路径
						 getfiles(path);  //获取文件
					}else{ //如果是图片可以做预览  其他暂不支持
						
					}
				}
				return;
			})
			//目录导航的点击事件
			$('.filepath ul').click(function(e){
				var f = e.target;
				if(f.tagName!='A') return;
				$('.filepath label center').text('正在加载文件。。。。。。。');
 				backtopath($(f).parent());
				//获取点击的目录名 如果是desktop的话 判断是第一个li  就是   /
				getfiles(getcurrentpath());
			});
			//分类按钮点击事件
			$('.navdiv select.checktype').change(function(){
				$('#contain .panel-body').empty();  //清空
				if($(this).val()=='all'){
					getfiles('/');
					setpath('/');
				}else if($(this).val()=='recy'){ //回收箱
					//state=0 的文件 		
				}else{
					setpath("分类："+$(this).find("option:selected").text());
					getfilesbytype($(this).val());
				}
			});
			//搜索按钮的点击事件
			$('.fileoperate button.seach').click(function(){
				var input = $(this).parent().siblings('input');
				if(input.val()==''){
					input.focus();
					return;
				} 
				$('#contain .panel-body').empty();  //清空
				setpath("搜索："+input.val());
				getfileslike(input.val());
			})
			//设置分类和搜索的导航
			function setpath(str){
				var lastpath =  $('.filepath ul').find('li.active');
				var newlastpath = lastpath.clone();		//复制最后目录结构
				$('.filepath ul').empty();   //删除所有
				if(str == '/'){
					//返回DESKTOP
					$('.filepath ul').append(newlastpath.text('DESKTOP'));
					return;
				}
				var a = $('<li><a href="javascript:void(0)"></a></li>'); 
				a.find('a').text("DESKTOP");
				$('.filepath ul').append(a).append(newlastpath.text(str)); //设置新的最后目录
				
			}
			//模糊查询文件
			function getfileslike(like){
				$.get('file/getfilelike.action?like='+like,function(res){
					console.log(res);
					if(res.code == 200){
						setfile(res.result,"file");
					}
					var str=(res.result.length>0)? '已加载:'.concat(res.result.length,'个文件') : '还没有任何文件哦！！';
					$('.filepath label center').text(str);
				})
			}
			//按照类型查找文件
			function getfilesbytype(type){
				$.get('file/getfilebytype.action?type='+type,function(res){
					if(res.code == 200){
						setfile(res.result,"file");
					}
					var str=(res.result.length>0)? '已加载:'.concat(res.result.length,'个文件') : '还没有任何文件哦！！';
					$('.filepath label center').text(str);
				})
			}
			//文件批量删除
			function deletefiles(){
			}
			//文件下载
			function downfile(fid){
				window.location.href = 'file/downfile.action?fid='+fid;
			}
			//导航回退目录 设置路径   将点击的目录设置不可点击  删除之后所有目录
			function backtopath(clickEle){ 
				$(clickEle).nextAll().remove();	//删除之后的目录名称
				var path = $(clickEle).text().trim();
				$(clickEle).empty().addClass('active').text(path)//设置当前名称不可点击
			}
			//进入文件夹  设置导航路径  将最后一个目录设置为可点击  再添加进入的文件夹名称设置不可点击
			function gotopath(path){
				 var lastpath =  $('.filepath ul').find('li.active');
				 var newlastpath = lastpath.clone();		//复制最后目录结构
				 var a = $('<a href="javascript:void(0)"></a>').text(lastpath.text()); 
				 lastpath.removeClass('active').empty().append(a);  //修改最后目录可点击
				 $('.filepath ul').append(newlastpath.text(path)); //设置新的最后目录
			}
			//如果当前目录下相同名字的话加上事件做标志
			function hadsamepath(path){
				var pathnames = $('.panel-body .fileitem .filename');
				for(var i=0,len=pathnames.length;i<len;i++){
					if(pathnames[i].innerText==path){
						return path.concat('_',nowtime());
					}
				}
				return path;
			}
			//获取路径下的所有文件
			function getfiles(path){
				// console.log(path)
				if(path=='') return;
				//清空当前界面的文件显示
				$('#contain .panel-body').empty();
				$.get('file/getfiles.action',{currentpath:path},function(res){
					// console.log(res);
					var num = 0;//设置加载的个数
					if(res.code == 200){
						//设置文件夹
						if(res.result.folder&&res.result.folder.length>0){
							setfile(res.result.folder,"folder");
							num=num+res.result.folder.length;
						}
						//设置文件
						if(res.result.file&&res.result.file.length>0){
							files = res.result.file;  //将所有文件设置到全局变量
							setfile(res.result.file,"file");
							num=num+res.result.file.length;
						}
					}else{
						console.log('还没有任何的资源哦')
					}
					var str=(num>0)? '已加载:'.concat(num,'个文件') : '还没有任何文件哦！！';
					$('.filepath label center').text(str);
				})
			}
			//将文件渲染到界面 
			function setfile(data,type){
				if(type=="file"){
					for(var i=0,len=data.length;i<len;i++){
						var item = $('#contain .fileitemtemple').clone();
						var file = data[i];
						item.removeClass("fileitemtemple").attr('data-id',file.id).attr('data-type','file');
						//根据文件名获取响应图标
						item.find('.fileimg use').attr('xlink:href',geticonbyfilename(file.fileName))
						item.find('.filename').attr('title',file.fileName).text(file.fileName); 
						var size = file.fileSize/1024/1024;
						if(size<1){
							size = String(Math.ceil(size*1024)).concat('KB');
						}else if(size>1024){
							size = String(Math.ceil(size/1024)).concat('G');
						}else{
							size = String(Math.ceil(size)).concat('M');
						}
						var content ='<p>大小: '+size+'</p>'+'<p>时间: '+file.updateTime.slice(0,16)+'</p>';
						item.find('.fileimg svg').attr('title',file.fileName)
						.attr('data-toggle','popover').attr('data-html','true')
						.attr('data-placement','right').attr('data-container', 'body').attr('data-content',content);
						$('#contain .panel-body').append(item);
						
					}
				}else if(type == "folder"){
					for(var i=0,len=data.length;i<len;i++){
						var item = $('#contain .fileitemtemple').clone();
						//只需要文件夹名称
						item.removeClass("fileitemtemple").attr('data-type','folder');
						item.find('.filename').attr('title',data[i]).text(data[i]);
						//删除详细按钮 
						item.find('.filectl .mesg').remove();
						$('#contain .panel-body').append(item);
					}
				}
			}
			//根据文件名获取相应图标
			function geticonbyfilename(filename){
				var s = String(filename);
				// console.log(s)
				if(s.endsWith(".mp3")||s.endsWith(".mp3"))
				return "#icon-file_music";
				if(s.endsWith(".doc")||s.endsWith(".docx"))
				return "#icon-file_word_office";
				if(s.endsWith(".xls")||s.endsWith(".xlsx"))
				return "#icon-file_excel_office";
				if(s.endsWith(".ppt")||s.endsWith(".pptx"))
				return "#icon-file_ppt_office";
				if(s.endsWith(".css"))
				return "#icon-file_css";
				if(s.endsWith(".pdf"))
				return "#icon-file_pdf";
				if(s.endsWith(".txt"))
				return "#icon-file_txt";
				if(s.endsWith(".mp4")||s.endsWith(".wmv"))
				return "#icon-file_video";
				if(s.endsWith(".mp4")||s.endsWith(".mp4"))
			    return "#icon-file_video";
				if(s.endsWith(".rar")||s.endsWith(".jar")||s.endsWith(".war")||s.endsWith(".zip"))
				return "#icon-file_zip"; 
				if(s.endsWith(".rtf"))
				return "#icon-file_rtf"; 
				if(s.endsWith(".exe"))
			    return "#icon-file_exe"; 
				if(s.endsWith(".java")||s.endsWith(".php")||s.endsWith(".class")||s.endsWith(".python")||s.endsWith(".js"))
				return "#icon-file_code"; 
				if(s.endsWith(".img")||s.endsWith(".png")||s.endsWith(".jpeg")||s.endsWith(".jpg"))
				return "#icon-file_pic"; 
				
				return "#icon-file_unknown";
			}
			//获取网盘的当前目录
			function getcurrentpath(){
			    var lis = $('.filepath ul li');
			    var currentpath = '/';
				//去除desktop 
			    for(var i=1;i<lis.length;i++){
				    currentpath = currentpath.concat(lis[i].innerText.trim(),'/');
			    }
				// console.log("当前目录："+currentpath);
				return currentpath;
			}
			//获取上传文件时文件在网盘中的路径
			function getfilepath(path){
				//获取当前网盘的路径
				var currentpath = getcurrentpath();
				//如果是文件夹的话有一个相对路径
				if(path == ''){
					return currentpath;
				}else{
					// 资源/font_q58kone7z4/demo.css   去除最后文件名称
					var p = path.substr(0,path.lastIndexOf('/')+1);
					return currentpath.concat(p);
				}
				
			}
			//文件上传任务列表
			function adduploadwork(filesize,filename,filesign){
				//模板
				var workli = $('#uploadwodkModel .modal-body .hidden').clone();
				workli.attr('workid',filesign);
				workli.attr('filesize',filesize);
				workli.removeClass('hidden');
				workli.find('.workname').text(filename);
				workli.find('.progress-bar').css({width:'0'});
				$('#uploadwodkModel .modal-body ul').prepend(workli);
				//任务加一
				var num = Number($('button.workbtn').find('span').text())+1;
				$('button.workbtn').find('span').text(num);
			};
			//监控上传的任务进度
			function uploadspeed(){
				//发送请求获取已上传文件大小
				$.get('file/uploadspeed.action',function(res){
					// console.log(res)
					if(res.code == 200){
						for(var key1 in res.result){
							var rs = res.result[key1];
							var uploadsize = 0;
							for(var key2 in rs){
								uploadsize = uploadsize+rs[key2];
							}
							var work = $('#uploadwodkModel ul li[workid="'+key1+'"]');
							if(work.length>0){
								// console.log(key1+": "+uploadsize);
								var width = work.find('.progress-bar').css('width');
								var filesize = parseInt( work.attr('filesize'));
								var p =(parseInt(width)/100+(uploadsize/filesize)*100);
								if(uploadsize>=filesize){
									//已上传完
 									work.find('.progress-bar').removeClass('progress-bar-info').addClass('progress-bar-success');
 									work.removeAttr('workid').removeAttr('filesize');
								}
								// console.log(parseInt(width)/100+(uploadsize/filesize)*100);
								work.find('.progress-bar').css({width:p+ "%"})
							}else{
								var ws = $('#uploadwodkModel ul li[workid]');
								if(ws.length==0){
									clearInterval(interval);//关闭定时器
									//刷新文件
									getfiles(getcurrentpath());
								} 
							}
						}
					} 
				})
				 
				//设置进度条百分比
			}
			//文件上传
			function upload(files){
				var len = files.length;
				for(var i=0,len=files.length;i<len;i++){
					var file = files[i];
					// console.log(file)
					//通过时间戳代表一个文件的file_sign
					var filepath = getfilepath(file.webkitRelativePath);
					var filesign =(new Date()).valueOf();
					var filedata = {
						fileSize:file.size,  //每个文件的总大小
						fileSign:filesign,
						fileName:file.name,
						filePath:filepath,
						order:1,
					}
					//添加一个上传任务
					adduploadwork(file.size,file.name,filesign);
				 
					if(file.size <= FIlE_CUT_MAXSIZE){
						// console.log(file)
						//上传文件 获取md5验证
						getmd5(file,filedata);
					}else{
						sliceupload(file,filedata);
						
					}
			 
				}
			}
			//分片上传
			function sliceupload(file,filedata){
				//分片上传
				var slices = Math.ceil(file.size/FIlE_CUT_MAXSIZE);
				//发送前面完整大小的文件片段
				for(var i=1;i<=slices;i++){
					var data = filedata;
					data.order = i;
					//上传文件 获取md5验证
					if(i<slices){
						cubf = file.slice((i-1)*FIlE_CUT_MAXSIZE,i*FIlE_CUT_MAXSIZE);
						getmd5(cubf,data);	
					}else{
						//发送剩余的
						cubf = file.slice((i-1)*FIlE_CUT_MAXSIZE,file.size);
						getmd5(cubf,data);	
					}
				}
			}
			//先判断后台是否存在文件
			function isupload(md5,file,filedata){
				var data = filedata;
				data.fileMd5 = md5;
				data.cutSize = file.size;
				$.post('file/isUploaded.action',data,function(res){
					if(res.code == 200){
						 console.log("秒传成功")
						//按分块个数 增加进度条进度   但是这里会有并发问题  获取width的时候   使用回调加延时
						//setTimeout(setspeed(data.filesign,data.num),parseInt(data.order)*200);
					}else{
						data.fid = res.result;
						uploadfile(md5,file,data);
					}
					// uploadspeed();
				});
				function setspeed(filesign,num){
					var wordk = $('#uploadwodkModel ul li[workid="'+filesign+'"]');
					var width = wordk.find('.progress-bar').css('width');
					var p =(parseInt(width)+(1/num)*100) + "%"
					console.log(p)
					wordk.find('.progress-bar').css({width:p})
				}
			}
			//单个文件上传
			function uploadfile(md5,file,filedata){
				var formdata = new FormData();
				formdata.append('file',file);
				formdata.append('fileMd5',md5);
				formdata.append('ordernum',filedata.order); //文件
				formdata.append('fid',filedata.fid);
				formdata.append('fileSign',filedata.fileSign);
				$.ajax({
					url:'file/upload.action',
					type:'post',
					cache:false,
					data:formdata,
					processData: false, 
					contentType: false,
					success : function(res) {
					    if(res.code = 200){
					    	// console.log("文件上传成功!");
					    }else{
							console.log("文件上传失败!");
						}
					},
					complete:function(){
					 
					}
				});
			}
			//计算文件md5
			function getmd5(file,filedata){
			    var data =  $.extend(true,{},filedata);
				var blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice,
				file = file,
				chunkSize = 1024*1024*2,     // Read in chunks of 2MB
				chunks = Math.ceil(file.size / chunkSize),
				currentChunk = 0,
				spark = new SparkMD5.ArrayBuffer(),
				fileReader = new FileReader();
				fileReader.onload = function (e) {
				    spark.append(e.target.result);                   // Append array buffer
				    currentChunk++;
				    if (currentChunk < chunks) {
				        loadNext();
				    } else {
						 // console.log(data.order);
						 isupload(spark.end(),file,data);
				    }
				};
				fileReader.onerror = function () {
				    console.warn('oops, something went wrong.');
				};
				
				function loadNext() {
				    var start = currentChunk * chunkSize,
				        end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;
				    fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
				}
				loadNext();
			}
			//获取当前时间
			function nowtime(){
				var myDate = new Date();             
				var year=myDate.getFullYear();        //获取当前年
				var month=myDate.getMonth()+1;   //获取当前月
				var date=myDate.getDate();            //获取当前日
				var h=myDate.getHours();              //获取当前小时数(0-23)
				var m=myDate.getMinutes();          //获取当前分钟数(0-59)
				var s=myDate.getSeconds();
				var now=year+''+month+''+date+'_'+h+''+m+''+s;
				return now;
			}
 