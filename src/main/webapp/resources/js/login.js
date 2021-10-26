$(function () {
    let accessToken = ($.cookie("jwt"));
    if (accessToken !== null && accessToken !== undefined) {
        window.location.replace("/")
    }

    $("input[type='password'][data-eye]").each(function (i) {
        var $this = $(this);

        $this.wrap($("<div/>", {
            style: 'position:relative'
        }));
        $this.css({
            paddingRight: 60
        });
        $this.after($("<div/>", {
            html: 'Show',
            class: 'btn btn-primary btn-sm',
            id: 'passeye-toggle-' + i,
            style: 'position:absolute;right:10px;top:50%;transform:translate(0,-50%);-webkit-transform:translate(0,-50%);-o-transform:translate(0,-50%);padding: 2px 7px;font-size:12px;cursor:pointer;'
        }));
        $this.after($("<input/>", {
            type: 'hidden',
            id: 'passeye-' + i
        }));
        $this.on("keyup paste", function () {
            $("#passeye-" + i).val($(this).val());
        });
        $("#passeye-toggle-" + i).on("click", function () {
            if ($this.hasClass("show")) {
                $("#passeye-toggle-0").html('Show')
                $this.attr('type', 'password');
                $this.removeClass("show");
                $(this).removeClass("btn-outline-primary");
            } else {
                $this.attr('type', 'text');
                $("#passeye-toggle-0").html('Hide')
                $this.val($("#passeye-" + i).val());
                $this.addClass("show");
                $(this).addClass("btn-outline-primary");
            }
        });
    });
});

$(document).ready(function(){
    $('#loginForm').submit(function( e ) {
        e.preventDefault();
        let formData = $(this).serializeArray();

        let data = {};
        $(formData).each(function(index, obj){
            if(data[obj.name] === undefined)
                data[obj.name] = obj.value;
        });

        $.ajax({
            url:"/api/auth/login",
            type:"POST",
            data:JSON.stringify(data),
            contentType:"application/json; charset=utf-8",
            dataType:"json",
            success: function(res){
                if (res.error.statusCode === 200 && res.data !== null) {
                    if (res.data.roles.includes("ROLE_ADMIN")) {
                        var now = new Date();
                        now.setTime(now.getTime() + 86400000);
                        $.cookie('jwt', 'Bearer ' + res.data.accessToken, {path: '/', expires: now})
                        window.location.replace("/")
                    } else alert("User không có quyền truy cập!")
                } else alert(res.error.message)
            },
            fail: function(){
                alert("BAD REQUEST")
            }
          })
    })
});