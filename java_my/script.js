/**
 * Created by anny on 12.11.14.
 */
var count = 0;
var count_checked = 0;
$(document).ready(function() {
    $('#InputText').keypress(function(e) {
        if (e.which == 13) {
            var item_text = $('#InputText:text').val();
            var item = $('<div class="Item">');
            item.append($('<input type="checkbox" class="ItemBox">'));
            item.append($('<span contenteditable=true>').text(item_text));
            item.append($('<input type="reset" class="X-button" value="X" hidden="true">'));
            var list = $('#list_set');
            list.append($(item));
            ++count;
            $('#counter').children(1).text(count + " items added");
        }
    });

    $('.Forms').submit(function(e) {
        return false;
    });

    $(document).on('change', ".ItemBox", function() {
        if ($(this).is(':checked')) {
            $(this).next().css('text-decoration', 'line-through');
            ++count_checked;
            if (count_checked == count) {
                $('#MarkBox').prop('checked', true);
            }
            $('#RemoveAll').show();
        } else {
            $(this).next().css('text-decoration', 'none');
            --count_checked;
            if (count_checked != count) {
                $('#MarkBox').prop('checked', false);
            }
            if (count_checked == 0 || count == 0) {
                $('#RemoveAll').hide();
            }
        }
    })

    $('#MarkBox').change(function() {
        if ($(this).is(':checked')) {
            $('.ItemBox').prop('checked', true);
            $('.ItemBox').next().css('text-decoration', 'line-through');
            count_checked = count;
            $('#RemoveAll').show();
        } else {
            $('.ItemBox').prop('checked', false);
            $('.ItemBox').next().css('text-decoration', 'none');
            count_checked = 0;
            $('#RemoveAll').hide();
        }
    })

    $(document).on('mouseover', '.Item', function() {
        $($(this).children()[2]).show();
    })

    $(document).on('mouseout', '.Item', function() {
        $($(this).children()[2]).hide();
    })

    $(document).on('click', '.X-button', function() {
        $($(this).parent()).remove();
        --count;
        if ($($($(this).prev()).prev()).is(':checked')) {
            --count_checked;
        }
        if (count_checked == 0) {
            $('#RemoveAll').hide();
        }
        $('#counter').children(1).text(count + " items added");
    })

    $('#RemoveAll').click(function() {
/*        $('#list_set').empty();*/
        for (var it in $($('#list_set').children())) {
            if ($(it).children()[0].is(':checked')) {
                $(it).remove();

                count = 0;
                count_checked = 0;
                $('#counter').children(1).text(count + " items added");
            }
        }
    })
});