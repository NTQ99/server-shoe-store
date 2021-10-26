$(function(){
    let accessToken = ($.cookie("jwt"));
    if (accessToken === null || accessToken === undefined) {
        window.location.replace("/login")
    }
});