/*
 Highcharts JS v4.1.8 (2015-08-20)
 Exporting module

 (c) 2010-2014 Torstein Honsi

 License: www.highcharts.com/license
 */
(function (g) {
    var A = g.Chart, t = g.addEvent, B = g.removeEvent, C = HighchartsAdapter.fireEvent, j = g.createElement,
        p = g.discardElement, v = g.css, l = g.merge, m = g.each, q = g.extend, E = g.splat, F = Math.max, k = document,
        D = window, G = g.isTouchDevice, H = g.Renderer.prototype.symbols,
        downloadAttrSupported = document.createElement('a').download !== undefined, each = Highcharts.each,
        pick = Highcharts.pick, seriesTypes = Highcharts.seriesTypes, r = g.getOptions(), y;

    q(r.lang, {
        printChart: "Print chart",
        downloadPNG: "Download PNG image",
        downloadCSV: 'Download CSV',
        downloadJPEG: "Download JPEG image",
        downloadPDF: "Download PDF document",
        downloadSVG: "Download SVG vector image",
        contextButtonTitle: "Chart context menu"
    });
    r.navigation =
        {
            menuStyle: {border: "1px solid #A0A0A0", background: "#FFFFFF", padding: "5px 0"},
            menuItemStyle: {padding: "0 10px", background: "none", color: "#303030", fontSize: G ? "14px" : "11px"},
            menuItemHoverStyle: {background: "#4572A5", color: "#FFFFFF"},
            buttonOptions: {
                symbolFill: "#E0E0E0",
                symbolSize: 14,
                symbolStroke: "#666",
                symbolStrokeWidth: 3,
                symbolX: 12.5,
                symbolY: 10.5,
                align: "right",
                buttonSpacing: 3,
                height: 22,
                theme: {fill: "white", stroke: "none"},
                verticalAlign: "top",
                width: 24
            }
        };
    r.exporting = {
        type: "image/png", url: "http://export.highcharts.com/",
        buttons: {
            contextButton: {
                menuClassName: "highcharts-contextmenu",
                symbol: "menu",
                _titleKey: "contextButtonTitle",
                menuItems: [{
                    textKey: "printChart", onclick: function () {
                        this.print()
                    }
                }, {separator: !0}, {
                    textKey: "downloadPNG", onclick: function () {
                        this.exportChart()
                    }
                }, {
                    textKey: "downloadJPEG", onclick: function () {
                        this.exportChart({type: "image/jpeg"})
                    }

                }, {
                    textKey: "downloadCSV", onclick: function () {
                        this.downloadCSV()
                    }
                }, {
                    textKey: "downloadPDF", onclick: function () {
                        this.exportChart({type: "application/pdf"})
                    }
                }, {
                    textKey: "downloadSVG", onclick: function () {
                        this.exportChart({type: "image/svg+xml"})
                    }
                }]
            }
        }
    };


    g.post = function (b, a, e) {
        var c, b = j("form", l({
            method: "post",
            action: b,
            enctype: "multipart/form-data"
        }, e), {display: "none"}, k.body);
        for (c in a) j("input", {type: "hidden", name: c, value: a[c]}, null, b);
        b.submit();
        p(b)
    };
    q(A.prototype, {
        sanitizeSVG: function (b) {
            return b.replace(/zIndex="[^"]+"/g, "").replace(/isShadow="[^"]+"/g, "").replace(/symbolName="[^"]+"/g, "").replace(/jQuery[0-9]+="[^"]+"/g, "").replace(/url\([^#]+#/g, "url(#").replace(/<svg /, '<svg xmlns:xlink="http://www.w3.org/1999/xlink" ').replace(/ (NS[0-9]+\:)?href=/g,
                " xlink:href=").replace(/\n/, " ").replace(/<\/svg>.*?$/, "</svg>").replace(/(fill|stroke)="rgba\(([ 0-9]+,[ 0-9]+,[ 0-9]+),([ 0-9\.]+)\)"/g, '$1="rgb($2)" $1-opacity="$3"').replace(/&nbsp;/g, "\u00a0").replace(/&shy;/g, "\u00ad").replace(/<IMG /g, "<image ").replace(/<(\/?)TITLE>/g, "<$1title>").replace(/height=([^" ]+)/g, 'height="$1"').replace(/width=([^" ]+)/g, 'width="$1"').replace(/hc-svg-href="([^"]+)">/g, 'xlink:href="$1"/>').replace(/ id=([^" >]+)/g, ' id="$1"').replace(/class=([^" >]+)/g, 'class="$1"').replace(/ transform /g,
                " ").replace(/:(path|rect)/g, "$1").replace(/style="([^"]+)"/g, function (a) {
                return a.toLowerCase()
            })
        }, getChartHTML: function () {
            return this.container.innerHTML
        }, getSVG: function (b) {
            var a = this, e, c, f, z, h, d = l(a.options, b), s = d.exporting.allowHTML;
            if (!k.createElementNS) k.createElementNS = function (a, b) {
                return k.createElement(b)
            };
            c = j("div", null, {
                position: "absolute",
                top: "-9999em",
                width: a.chartWidth + "px",
                height: a.chartHeight + "px"
            }, k.body);
            f = a.renderTo.style.width;
            h = a.renderTo.style.height;
            f = d.exporting.sourceWidth ||
                d.chart.width || /px$/.test(f) && parseInt(f, 10) || 600;
            h = d.exporting.sourceHeight || d.chart.height || /px$/.test(h) && parseInt(h, 10) || 400;
            q(d.chart, {animation: !1, renderTo: c, forExport: !s, width: f, height: h});
            d.exporting.enabled = !1;
            delete d.data;
            d.series = [];
            m(a.series, function (a) {
                z = l(a.options, {animation: !1, enableMouseTracking: !1, showCheckbox: !1, visible: a.visible});
                z.isInternal || d.series.push(z)
            });
            b && m(["xAxis", "yAxis"], function (a) {
                m(E(b[a]), function (b, c) {
                    d[a][c] = l(d[a][c], b)
                })
            });
            e = new g.Chart(d, a.callback);
            m(["xAxis",
                "yAxis"], function (b) {
                m(a[b], function (a, c) {
                    var d = e[b][c], f = a.getExtremes(), h = f.userMin, f = f.userMax;
                    d && (h !== void 0 || f !== void 0) && d.setExtremes(h, f, !0, !1)
                })
            });
            f = e.getChartHTML();
            d = null;
            e.destroy();
            p(c);
            if (s && (c = f.match(/<\/svg>(.*?$)/))) c = '<foreignObject x="0" y="0 width="200" height="200"><body xmlns="http://www.w3.org/1999/xhtml">' + c[1] + "</body></foreignObject>", f = f.replace("</svg>", c + "</svg>");
            f = this.sanitizeSVG(f);
            return f = f.replace(/(url\(#highcharts-[0-9]+)&quot;/g, "$1").replace(/&quot;/g, "'")
        },
        downloadCSV: function () {
            var csv = this.getCSV(true);
            this.getContent(
                this,
                'data:text/csv,\uFEFF' + encodeURIComponent(csv),
                'csv',
                csv,
                'text/csv'
            );
        },

        getContent: function (chart, href, extension, content, MIME) {
            var a,
                blobObject,
                name,
                options = (chart.options.exporting || {}).csv || {},
                url = options.url || 'http://www.highcharts.com/studies/csv-export/download.php';

            if (chart.options.exporting.filename) {
                name = chart.options.exporting.filename;
            } else if (chart.title) {
                name = chart.title.textStr.replace(/ /g, '-').toLowerCase();
            } else {
                name = 'chart';
            }

            // MS specific. Check this first because of bug with Edge (#76)
            if (window.Blob && window.navigator.msSaveOrOpenBlob) {
                // Falls to msSaveOrOpenBlob if download attribute is not supported
                blobObject = new Blob([content]);
                window.navigator.msSaveOrOpenBlob(blobObject, name + '.' + extension);

                // Download attribute supported
            } else if (downloadAttrSupported) {
                a = document.createElement('a');
                a.href = href;
                a.download = name + '.' + extension;
                chart.container.append(a); // #111
                a.click();
                a.remove();

            } else {
                // Fall back to server side handling
                Highcharts.post(url, {
                    data: content,
                    type: MIME,
                    extension: extension
                });
            }
        },
        getDataRows: function () {
            var options = (this.options.exporting || {}).csv || {},
                xAxis,
                xAxes = this.xAxis,
                rows = {},
                rowArr = [],
                dataRows,
                names = [],
                i,
                x,
                xTitle,
                // Options
                dateFormat = options.dateFormat || '%Y-%m-%d %H:%M:%S',
                columnHeaderFormatter = options.columnHeaderFormatter || function (item, key, keyLength) {
                    if (item instanceof Highcharts.Axis) {
                        return (item.options.title && item.options.title.text) ||
                            (item.isDatetimeAxis ? 'DateTime' : 'Category');
                    }
                    return item ?
                        item.name + (keyLength > 1 ? ' (' + key + ')' : '') :
                        'Category';
                },
                xAxisIndices = [];

            // Loop the series and index values
            i = 0;
            each(this.series, function (series) {
                var keys = series.options.keys,
                    pointArrayMap = keys || series.pointArrayMap || ['y'],
                    valueCount = pointArrayMap.length,
                    requireSorting = series.requireSorting,
                    categoryMap = {},
                    xAxisIndex = xAxes.indexOf ? xAxes.indexOf(series.xAxis) : [].indexOf.call(xAxes, series.xAxis);
                j;

                // Map the categories for value axes
                each(pointArrayMap, function (prop) {
                    categoryMap[prop] = (series[prop + 'Axis'] && series[prop + 'Axis'].categories) || [];
                });

                if (series.options.includeInCSVExport !== false && series.visible !== false) { // #55

                    // Build a lookup for X axis index and the position of the first
                    // series that belongs to that X axis. Includes -1 for non-axis
                    // series types like pies.
                    if (![].find.call(xAxisIndices, function (index) {
                            return index[0] === xAxisIndex;
                        })) {
                        xAxisIndices.push([xAxisIndex, i]);
                    }

                    // Add the column headers, usually the same as series names
                    j = 0;
                    while (j < valueCount) {
                        names.push(columnHeaderFormatter(series, pointArrayMap[j], pointArrayMap.length));
                        j = j + 1;
                    }

                    each(series.points, function (point, pIdx) {
                        var key = requireSorting ? point.x : pIdx,
                            prop,
                            val;

                        j = 0;

                        if (!rows[key]) {
                            // Generate the row
                            rows[key] = [];
                            // Contain the X values from one or more X axes
                            rows[key].xValues = [];
                        }
                        rows[key].x = point.x;
                        rows[key].xValues[xAxisIndex] = point.x;

                        // Pies, funnels, geo maps etc. use point name in X row
                        if (!series.xAxis || series.exportKey === 'name') {
                            rows[key].name = point.name;
                        }

                        while (j < valueCount) {
                            prop = pointArrayMap[j]; // y, z etc
                            val = point[prop];
                            rows[key][i + j] = pick(categoryMap[prop][val], val); // Pick a Y axis category if present
                            j = j + 1;
                        }

                    });
                    i = i + j;
                }
            });

            // Make a sortable array
            for (x in rows) {
                if (rows.hasOwnProperty(x)) {
                    rowArr.push(rows[x]);
                }
            }

            var binding, xAxisIndex, column;
            dataRows = [names];

            i = xAxisIndices.length;
            while (i--) { // Start from end to splice in
                xAxisIndex = xAxisIndices[i][0];
                column = xAxisIndices[i][1];
                xAxis = xAxes[xAxisIndex];

                // Sort it by X values
                rowArr.sort(function (a, b) {
                    return a.xValues[xAxisIndex] - b.xValues[xAxisIndex];
                });

                // Add header row
                xTitle = columnHeaderFormatter(xAxis);
                //dataRows = [[xTitle].concat(names)];
                dataRows[0].splice(column, 0, xTitle);

                // Add the category column
                each(rowArr, function (row) {

                    var category = row.name;
                    if (!category) {
                        if (xAxis.isDatetimeAxis) {
                            if (row.x instanceof Date) {
                                row.x = row.x.getTime();
                            }
                            category = this.dateFormat(dateFormat, row.x);
                        } else if (xAxis.categories) {
                            category = pick(
                                xAxis.names[row.x],
                                xAxis.categories[row.x],
                                row.x
                            )
                        } else {
                            category = row.x;
                        }
                    }

                    // Add the X/date/category
                    row.splice(column, 0, category);
                });
            }
            dataRows = dataRows.concat(rowArr);

            return dataRows;
        },
        dateFormat: function (format, timestamp, capitalize) {
            if (!H.defined(timestamp) || isNaN(timestamp)) {
                return H.defaultOptions.lang.invalidDate || '';
            }
            format = H.pick(format, '%Y-%m-%d %H:%M:%S');

            var D = H.Date,
                date = new D(timestamp - H.getTZOffset(timestamp)),
                // get the basic time values
                hours = date[D.hcGetHours](),
                day = date[D.hcGetDay](),
                dayOfMonth = date[D.hcGetDate](),
                month = date[D.hcGetMonth](),
                fullYear = date[D.hcGetFullYear](),
                lang = H.defaultOptions.lang,
                langWeekdays = lang.weekdays,
                shortWeekdays = lang.shortWeekdays,
                pad = H.pad,

                // List all format keys. Custom formats can be added from the outside.
                replacements = H.extend({

                        //-- Day
                        // Short weekday, like 'Mon'
                        'a': shortWeekdays ?
                            shortWeekdays[day] : langWeekdays[day].substr(0, 3),
                        // Long weekday, like 'Monday'
                        'A': langWeekdays[day],
                        // Two digit day of the month, 01 to 31
                        'd': pad(dayOfMonth),
                        // Day of the month, 1 through 31
                        'e': pad(dayOfMonth, 2, ' '),
                        'w': day,

                        // Week (none implemented)
                        //'W': weekNumber(),

                        //-- Month
                        // Short month, like 'Jan'
                        'b': lang.shortMonths[month],
                        // Long month, like 'January'
                        'B': lang.months[month],
                        // Two digit month number, 01 through 12
                        'm': pad(month + 1),

                        //-- Year
                        // Two digits year, like 09 for 2009
                        'y': fullYear.toString().substr(2, 2),
                        // Four digits year, like 2009
                        'Y': fullYear,

                        //-- Time
                        // Two digits hours in 24h format, 00 through 23
                        'H': pad(hours),
                        // Hours in 24h format, 0 through 23
                        'k': hours,
                        // Two digits hours in 12h format, 00 through 11
                        'I': pad((hours % 12) || 12),
                        // Hours in 12h format, 1 through 12
                        'l': (hours % 12) || 12,
                        // Two digits minutes, 00 through 59
                        'M': pad(date[D.hcGetMinutes]()),
                        // Upper case AM or PM
                        'p': hours < 12 ? 'AM' : 'PM',
                        // Lower case AM or PM
                        'P': hours < 12 ? 'am' : 'pm',
                        // Two digits seconds, 00 through  59
                        'S': pad(date.getSeconds()),
                        // Milliseconds (naming from Ruby)
                        'L': pad(Math.round(timestamp % 1000), 3)
                    },

                    /**
                     * A hook for defining additional date format specifiers. New
                     * specifiers are defined as key-value pairs by using the specifier
                     * as key, and a function which takes the timestamp as value. This
                     * function returns the formatted portion of the date.
                     *
                     * @type {Object}
                     * @name dateFormats
                     * @memberOf Highcharts
                     * @sample highcharts/global/dateformats/ Adding support for week
                     * number
                     */
                    H.dateFormats
                );


            // Do the replaces
            H.objectEach(replacements, function (val, key) {
                // Regex would do it in one line, but this is faster
                while (format.indexOf('%' + key) !== -1) {
                    format = format.replace(
                        '%' + key,
                        typeof val === 'function' ? val(timestamp) : val
                    );
                }

            });

            // Optionally capitalize the string and return
            return capitalize ?
                format.substr(0, 1).toUpperCase() + format.substr(1) :
                format;
        },

        getSVGForExport: function (b, a) {
            var e = this.options.exporting;
            return this.getSVG(l({chart: {borderRadius: 0}}, e.chartOptions, a, {
                exporting: {
                    sourceWidth: b && b.sourceWidth || e.sourceWidth,
                    sourceHeight: b && b.sourceHeight || e.sourceHeight
                }
            }))
        },
        downloadCSV: function () {
            var csv = this.getCSV(true);
            this.getContent(
                this,
                'data:text/csv,\uFEFF' + encodeURIComponent(csv),
                'csv',
                csv,
                'text/csv'
            );
        },
        getCSV: function (useLocalDecimalPoint) {
            var csv = '',
                rows = this.getDataRows(),
                options = (this.options.exporting || {}).csv || {},
                itemDelimiter = options.itemDelimiter || ',', // use ';' for direct import to Excel
                lineDelimiter = options.lineDelimiter || '\n'; // '\n' isn't working with the js csv data extraction

            // Transform the rows to CSV
            each(rows, function (row, i) {
                var val = '',
                    j = row.length,
                    n = useLocalDecimalPoint ? (1.1).toLocaleString()[1] : '.';
                while (j--) {
                    val = row[j];
                    // if (typeof val === "string") { // is value String type - add symbol "
                    //     val = '"' + val + '"';
                    // }
                    if (typeof val === 'number') {
                        if (n === ',') {
                            val = val.toString().replace(",", ".");
                        }
                    }
                    // if (typeof val === 'undefined' || !val) { // if value in empty - set NaN
                    //     val = 'NaN';
                    // }

                    row[j] = val;
                }
                // Add the values
                csv += row.join(itemDelimiter);

                // Add the line delimiter
                if (i < rows.length - 1) {
                    csv += lineDelimiter;
                }
            });
            return csv;
        },
        exportChart: function (b, a) {
            var e = this.getSVGForExport(b, a), b = l(this.options.exporting, b);
            g.post(b.url, {
                filename: b.filename || "chart",
                type: b.type,
                width: b.width || 0,
                scale: b.scale || 2,
                svg: e
            }, b.formAttributes)
        }, print: function () {
            var b = this, a = b.container, e = [], c = a.parentNode, f = k.body,
                g = f.childNodes;
            if (!b.isPrinting) b.isPrinting = !0, C(b, "beforePrint"), m(g, function (a, b) {
                if (a.nodeType === 1) e[b] = a.style.display, a.style.display = "none"
            }), f.appendChild(a), D.focus(), D.print(), setTimeout(function () {
                c.appendChild(a);
                m(g, function (a, b) {
                    if (a.nodeType === 1) a.style.display = e[b]
                });
                b.isPrinting = !1;
                C(b, "afterPrint")
            }, 1E3)
        }, contextMenu: function (b, a, e, c, f, g, h) {
            var d = this, s = d.options.navigation, l = s.menuItemStyle, n = d.chartWidth, o = d.chartHeight,
                k = "cache-" + b, i = d[k], u = F(f, g), w, x, p, r = function (a) {
                    d.pointer.inClass(a.target,
                        b) || x()
                };
            if (!i) d[k] = i = j("div", {className: b}, {
                position: "absolute",
                zIndex: 1E3,
                padding: u + "px"
            }, d.container), w = j("div", null, q({
                MozBoxShadow: "3px 3px 10px #888",
                WebkitBoxShadow: "3px 3px 10px #888",
                boxShadow: "3px 3px 10px #888"
            }, s.menuStyle), i), x = function () {
                v(i, {display: "none"});
                h && h.setState(0);
                d.openMenu = !1
            }, t(i, "mouseleave", function () {
                p = setTimeout(x, 500)
            }), t(i, "mouseenter", function () {
                clearTimeout(p)
            }), t(document, "mouseup", r), t(d, "destroy", function () {
                B(document, "mouseup", r)
            }), m(a, function (a) {
                if (a) {
                    var b =
                        a.separator ? j("hr", null, null, w) : j("div", {
                            onmouseover: function () {
                                v(this, s.menuItemHoverStyle)
                            }, onmouseout: function () {
                                v(this, l)
                            }, onclick: function (b) {
                                b.stopPropagation();
                                x();
                                a.onclick && a.onclick.apply(d, arguments)
                            }, innerHTML: a.text || d.options.lang[a.textKey]
                        }, q({cursor: "pointer"}, l), w);
                    d.exportDivElements.push(b)
                }
            }), d.exportDivElements.push(w, i), d.exportMenuWidth = i.offsetWidth, d.exportMenuHeight = i.offsetHeight;
            a = {display: "block"};
            e + d.exportMenuWidth > n ? a.right = n - e - f - u + "px" : a.left = e - u + "px";
            c + g + d.exportMenuHeight >
            o && h.alignOptions.verticalAlign !== "top" ? a.bottom = o - c - u + "px" : a.top = c + g - u + "px";
            v(i, a);
            d.openMenu = !0
        }, addButton: function (b) {
            var a = this, e = a.renderer, c = l(a.options.navigation.buttonOptions, b), f = c.onclick, k = c.menuItems,
                h, d, m = {
                    stroke: c.symbolStroke,
                    fill: c.symbolFill
                }, j = c.symbolSize || 12;
            if (!a.btnCount) a.btnCount = 0;
            if (!a.exportDivElements) a.exportDivElements = [], a.exportSVGElements = [];
            if (c.enabled !== !1) {
                var n = c.theme, o = n.states, p = o && o.hover, o = o && o.select, i;
                delete n.states;
                f ? i = function (b) {
                    b.stopPropagation();
                    f.call(a, b)
                } : k && (i = function () {
                    a.contextMenu(d.menuClassName, k, d.translateX, d.translateY, d.width, d.height, d);
                    d.setState(2)
                });
                c.text && c.symbol ? n.paddingLeft = g.pick(n.paddingLeft, 25) : c.text || q(n, {
                    width: c.width,
                    height: c.height,
                    padding: 0
                });
                d = e.button(c.text, 0, 0, i, n, p, o).attr({
                    title: a.options.lang[c._titleKey],
                    "stroke-linecap": "round"
                });
                d.menuClassName = b.menuClassName || "highcharts-menu-" + a.btnCount++;
                c.symbol && (h = e.symbol(c.symbol, c.symbolX - j / 2, c.symbolY - j / 2, j, j).attr(q(m, {
                    "stroke-width": c.symbolStrokeWidth ||
                    1, zIndex: 1
                })).add(d));
                d.add().align(q(c, {width: d.width, x: g.pick(c.x, y)}), !0, "spacingBox");
                y += (d.width + c.buttonSpacing) * (c.align === "right" ? -1 : 1);
                a.exportSVGElements.push(d, h)
            }
        }, destroyExport: function (b) {
            var b = b.target, a, e;
            for (a = 0; a < b.exportSVGElements.length; a++) if (e = b.exportSVGElements[a]) e.onclick = e.ontouchstart = null, b.exportSVGElements[a] = e.destroy();
            for (a = 0; a < b.exportDivElements.length; a++) e = b.exportDivElements[a], B(e, "mouseleave"), b.exportDivElements[a] = e.onmouseout = e.onmouseover = e.ontouchstart =
                e.onclick = null, p(e)
        }
    });
    H.menu = function (b, a, e, c) {
        return ["M", b, a + 2.5, "L", b + e, a + 2.5, "M", b, a + c / 2 + 0.5, "L", b + e, a + c / 2 + 0.5, "M", b, a + c - 1.5, "L", b + e, a + c - 1.5]
    };
    A.prototype.callbacks.push(function (b) {
        var a, e = b.options.exporting, c = e.buttons;
        y = 0;
        if (e.enabled !== !1) {
            for (a in c) b.addButton(c[a]);
            t(b, "destroy", b.destroyExport)
        }
    })
})(Highcharts);