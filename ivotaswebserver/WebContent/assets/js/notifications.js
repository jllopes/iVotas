$('body').css("overflow-x","hidden");

function displayNotification(status, content, showTime) {
    
    var element = $('<div class="notification">'+content+'</div>'),
        existingCount = 0,
        newPosition = 0;              
    if ($('.notification')[0]) {
        existingCount = $('.notification').length;
    };
    newPosition = existingCount * 90;
    $(element).css('bottom', newPosition);
        
    switch(status) {
            case "error":
                $(element).addClass('notification-error');
                break;
            case "success":
                $(element).addClass('notification-success');
                break;
            case "warning":
                $(element).addClass('notification-warning');
                break;
            case "info":
                $(element).addClass('notification-info');
                break;
            default:
                $(element).addClass('notification-'+status);
    }
    
    $('body').append(element);
    
    element.animate({
        "right":5  
    }, 500).animate({
        "right":0
    }, 200).delay(showTime).animate({
        "right":5
    }, 200).animate({
        "right":-300
    }, 500, function() {
        $(this).remove();
    });
}