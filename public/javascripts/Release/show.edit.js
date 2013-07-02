$(function() {
    $(document).on("click", "a.delete-file", function() {
        if (!$(this).hasClass('active')) {
            $(this).addClass('active');
            var fileId = $(this).attr('id').substring(19);
            $.post('/file/' + fileId + '/delete', function(data) {
                if (data.status === 0) {
                    $('li#file-' + fileId).remove();
                } else {                
                    alert("An error occured : [" + wsresult.status +"] " + wsresult.message);
                }
                $(this).removeClass('active');
            });
        }
    });
});