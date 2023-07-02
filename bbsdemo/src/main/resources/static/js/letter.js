$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	//异步发送post请求
	$.post(
	    "/community/letter/send",
	    {"toName":toName, "content":content},
	    function(data){
	        data = $.parseJSON(data);
	        if(data.code == 0){
	        //表示成功发送
	            $("#hintBody").text("Send successfully!");
	        }else{
	            $("#hintBody").text(data.msg);
	        }

	        //不论成功或失败都刷新一下页面
	        $("#hintModal").modal("show");
            setTimeout(function(){
            	$("#hintModal").modal("hide");
            	location.reload();
            }, 2000);

	    }
	);
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}