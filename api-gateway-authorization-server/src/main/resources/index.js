$(document).ready(function () {
    $('#submit_form').on('submit', function (e) {
        e.preventDefault();

        $.ajax({
            url: "/v1/account",
            method: "POST",
            data: {
                username: $('#username').val(),
                previous_password: $('#previous_password').val(),
                proposed_password: $('#proposed_password').val()
            },
            success: function (result) {
                showResponseMessage(result);

                if (result.acknowledged === true) {
                    $("#change_password").hide();
                    $("#usage").show();
                }
            }
        });
    });

    function showResponseMessage(result) {
        var alert = $("#alert");
        alert.show("slow").delay(3000).hide("slow");

        if (result.acknowledged === true) {
            alert.attr("class", "alert alert-success");
        } else {
            alert.attr("class", "alert alert-danger")
        }

        alert.text(result.message)
    }

    var password = document.getElementById("proposed_password"),
        confirm_password = document.getElementById("confirm_password");

    function validatePassword() {
        if (password.value !== confirm_password.value) {
            confirm_password.setCustomValidity("Passwords Don't Match");
        } else {
            confirm_password.setCustomValidity('');
        }
    }

    password.onchange = validatePassword;
    confirm_password.onkeyup = validatePassword;
});