$(function () {
    $('select#new-user-id').change(function() {
        if ($(this).val() == -1) {
            $('select#new-user-role-id').hide();
        } else {
            $('select#new-user-role-id').show();
        }
    });
    
    $('select#new-user-id').val(-1);
    $('select#new-user-role-id').hide();
});