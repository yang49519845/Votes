    function showImportModel() {
        $('#importModel').modal('show');
    }
    //定义按钮事件
    function excelImport(voteId) {
        var fileObj = document.getElementById("file").files[0]; // js 获取文件对象
        var url = window.location.origin + "/item/import"; // 接收上传文件的后台地址
        var form = new FormData(); // FormData 对象
        if (form) {
            form.append("file", fileObj); // 文件对象
        }
        xhr = new XMLHttpRequest(); // XMLHttpRequest 对象
        xhr.open("post", url, false); //post方式，url为服务器请求地址，true 该参数规定请求是否异步处理。
        xhr.onload = uploadComplete; //请求完成
        xhr.onerror = uploadFailed; //请求失败
        xhr.upload.onprogress = progressFunction; //【上传进度调用方法实现】
        xhr.upload.onloadstart = function () { //上传开始执行方法
            ot = new Date().getTime(); //设置上传开始时间
            oloaded = 0; //设置上传开始时，以上传的文件大小为0
        };
        xhr.setRequestHeader("itemId", voteId);
        xhr.send(form); //开始上传，发送form数据
    }

    //上传成功响应
    function uploadComplete(evt) {
        if (evt.target.status) {
            window.location.reload();
        } else {
            alert("上传失败！");
        }

    }
    //上传失败
    function uploadFailed(evt) {
        alert("上传失败！");
    }

    //上传进度实现方法，上传过程中会频繁调用该方法
    function progressFunction(evt) {
        var progressBar = document.getElementById("progressBar");
        var percentageDiv = document.getElementById("percentage");
        // event.total是需要传输的总字节，event.loaded是已经传输的字节。如果event.lengthComputable不为真，则event.total等于0
        if (evt.lengthComputable) { //
            progressBar.max = evt.total;
            progressBar.value = evt.loaded;
            percentageDiv.innerHTML = Math.round(evt.loaded / evt.total * 100) + "%";
        }
        var time = document.getElementById("time");
        var nt = new Date().getTime(); //获取当前时间
        var pertime = (nt - ot) / 1000; //计算出上次调用该方法时到现在的时间差，单位为s
        ot = new Date().getTime(); //重新赋值时间，用于下次计算
        var perload = evt.loaded - oloaded; //计算该分段上传的文件大小，单位b
        oloaded = evt.loaded; //重新赋值已上传文件大小，用以下次计算
        //上传速度计算
        var speed = perload / pertime; //单位b/s
        var bspeed = speed;
        var units = 'b/s'; //单位名称
        if (speed / 1024 > 1) {
            speed = speed / 1024;
            units = 'k/s';
        }
        if (speed / 1024 > 1) {
            speed = speed / 1024;
            units = 'M/s';
        }
        speed = speed.toFixed(1);
        //剩余时间
        var resttime = ((evt.total - evt.loaded) / bspeed).toFixed(1);
        time.innerHTML = '，速度：' + speed + units + '，剩余时间：' + resttime + 's';
        if (bspeed == 0) time.innerHTML = '上传已取消';
    }

    // 表格方法
    function getIdSelections() {
        return $.map($table.bootstrapTable('getSelections'), function (row) {
            return row.id
        })
    }

    function showModel(id) {
        $(id).modal('show');
    }

    /**
     * 初始化表格
     */
    function initTable() {
        $table.bootstrapTable('destroy').bootstrapTable({
            height: 550, // 初始高度
            clickToSelect: true,
            minimumCountColumns: 2,
            idField: 'id',
            sidePagination: 'server',
            // locale: $('#locale').val(), // 语言类型
            // 列操作栏
            columns: [
                {
                    field: 'state',
                    checkbox: true,
                    align: 'center',
                    valign: 'middle'
                },
                {
                    title: '序号',
                    field: 'voteItemId',
                    align: 'center',
                    valign: 'middle',
                },
                {
                    field: 'name',
                    title: '姓名',
                    align: 'center'
                },
                {
                    field: 'attr1',
                    title: '性别',
                    align: 'center',
                },
                {
                    field: 'attr2',
                    title: '部门',
                    align: 'center',
                },
                {
                    field: 'attr3',
                    title: '职称',
                    align: 'center',
                },
                {
                    field: 'arrt4',
                    title: '年龄',
                    align: 'center',
                },
                {
                    field: 'arrt4',
                    title: '入职年限',
                    align: 'center',
                }
                // {
                //     field: '',
                //     title: '投票规则',
                //     align: 'center',
                //     formatter:function(value, row, index){
                //         return [
                //              '<a class="remove" href="javascript:void(0)" title="Remove">',
                //                 '投票',
                //              '</a>'
                //         ].join('')
                //     }
                // }
            ],
            data: data
        })
    }