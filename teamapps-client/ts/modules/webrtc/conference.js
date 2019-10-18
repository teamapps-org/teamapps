(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory();
	else if(typeof define === 'function' && define.amd)
		define("conference", [], factory);
	else if(typeof exports === 'object')
		exports["conference"] = factory();
	else
		root["conference"] = factory();
})(window, function() {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
/******/ 		}
/******/ 	};
/******/
/******/ 	// define __esModule on exports
/******/ 	__webpack_require__.r = function(exports) {
/******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 		}
/******/ 		Object.defineProperty(exports, '__esModule', { value: true });
/******/ 	};
/******/
/******/ 	// create a fake namespace object
/******/ 	// mode & 1: value is a module id, require it
/******/ 	// mode & 2: merge all properties of value into the ns
/******/ 	// mode & 4: return value when already ns object
/******/ 	// mode & 8|1: behave like require
/******/ 	__webpack_require__.t = function(value, mode) {
/******/ 		if(mode & 1) value = __webpack_require__(value);
/******/ 		if(mode & 8) return value;
/******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
/******/ 		var ns = Object.create(null);
/******/ 		__webpack_require__.r(ns);
/******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
/******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
/******/ 		return ns;
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = "./src/conference.ts");
/******/ })
/************************************************************************/
/******/ ({

/***/ "./node_modules/after/index.js":
/*!*************************************!*\
  !*** ./node_modules/after/index.js ***!
  \*************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = after

function after(count, callback, err_cb) {
    var bail = false
    err_cb = err_cb || noop
    proxy.count = count

    return (count === 0) ? callback() : proxy

    function proxy(err, result) {
        if (proxy.count <= 0) {
            throw new Error('after called too many times')
        }
        --proxy.count

        // after first error, rest are passed to err_cb
        if (err) {
            bail = true
            callback(err)
            // future error callbacks will go to error handler
            callback = err_cb
        } else if (proxy.count === 0 && !bail) {
            callback(null, result)
        }
    }
}

function noop() {}


/***/ }),

/***/ "./node_modules/arraybuffer.slice/index.js":
/*!*************************************************!*\
  !*** ./node_modules/arraybuffer.slice/index.js ***!
  \*************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

/**
 * An abstraction for slicing an arraybuffer even when
 * ArrayBuffer.prototype.slice is not supported
 *
 * @api public
 */

module.exports = function(arraybuffer, start, end) {
  var bytes = arraybuffer.byteLength;
  start = start || 0;
  end = end || bytes;

  if (arraybuffer.slice) { return arraybuffer.slice(start, end); }

  if (start < 0) { start += bytes; }
  if (end < 0) { end += bytes; }
  if (end > bytes) { end = bytes; }

  if (start >= bytes || start >= end || bytes === 0) {
    return new ArrayBuffer(0);
  }

  var abv = new Uint8Array(arraybuffer);
  var result = new Uint8Array(end - start);
  for (var i = start, ii = 0; i < end; i++, ii++) {
    result[ii] = abv[i];
  }
  return result.buffer;
};


/***/ }),

/***/ "./node_modules/backo2/index.js":
/*!**************************************!*\
  !*** ./node_modules/backo2/index.js ***!
  \**************************************/
/*! no static exports found */
/***/ (function(module, exports) {


/**
 * Expose `Backoff`.
 */

module.exports = Backoff;

/**
 * Initialize backoff timer with `opts`.
 *
 * - `min` initial timeout in milliseconds [100]
 * - `max` max timeout [10000]
 * - `jitter` [0]
 * - `factor` [2]
 *
 * @param {Object} opts
 * @api public
 */

function Backoff(opts) {
  opts = opts || {};
  this.ms = opts.min || 100;
  this.max = opts.max || 10000;
  this.factor = opts.factor || 2;
  this.jitter = opts.jitter > 0 && opts.jitter <= 1 ? opts.jitter : 0;
  this.attempts = 0;
}

/**
 * Return the backoff duration.
 *
 * @return {Number}
 * @api public
 */

Backoff.prototype.duration = function(){
  var ms = this.ms * Math.pow(this.factor, this.attempts++);
  if (this.jitter) {
    var rand =  Math.random();
    var deviation = Math.floor(rand * this.jitter * ms);
    ms = (Math.floor(rand * 10) & 1) == 0  ? ms - deviation : ms + deviation;
  }
  return Math.min(ms, this.max) | 0;
};

/**
 * Reset the number of attempts.
 *
 * @api public
 */

Backoff.prototype.reset = function(){
  this.attempts = 0;
};

/**
 * Set the minimum duration
 *
 * @api public
 */

Backoff.prototype.setMin = function(min){
  this.ms = min;
};

/**
 * Set the maximum duration
 *
 * @api public
 */

Backoff.prototype.setMax = function(max){
  this.max = max;
};

/**
 * Set the jitter
 *
 * @api public
 */

Backoff.prototype.setJitter = function(jitter){
  this.jitter = jitter;
};



/***/ }),

/***/ "./node_modules/base64-arraybuffer/lib/base64-arraybuffer.js":
/*!*******************************************************************!*\
  !*** ./node_modules/base64-arraybuffer/lib/base64-arraybuffer.js ***!
  \*******************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

/*
 * base64-arraybuffer
 * https://github.com/niklasvh/base64-arraybuffer
 *
 * Copyright (c) 2012 Niklas von Hertzen
 * Licensed under the MIT license.
 */
(function(){
  "use strict";

  var chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

  // Use a lookup table to find the index.
  var lookup = new Uint8Array(256);
  for (var i = 0; i < chars.length; i++) {
    lookup[chars.charCodeAt(i)] = i;
  }

  exports.encode = function(arraybuffer) {
    var bytes = new Uint8Array(arraybuffer),
    i, len = bytes.length, base64 = "";

    for (i = 0; i < len; i+=3) {
      base64 += chars[bytes[i] >> 2];
      base64 += chars[((bytes[i] & 3) << 4) | (bytes[i + 1] >> 4)];
      base64 += chars[((bytes[i + 1] & 15) << 2) | (bytes[i + 2] >> 6)];
      base64 += chars[bytes[i + 2] & 63];
    }

    if ((len % 3) === 2) {
      base64 = base64.substring(0, base64.length - 1) + "=";
    } else if (len % 3 === 1) {
      base64 = base64.substring(0, base64.length - 2) + "==";
    }

    return base64;
  };

  exports.decode =  function(base64) {
    var bufferLength = base64.length * 0.75,
    len = base64.length, i, p = 0,
    encoded1, encoded2, encoded3, encoded4;

    if (base64[base64.length - 1] === "=") {
      bufferLength--;
      if (base64[base64.length - 2] === "=") {
        bufferLength--;
      }
    }

    var arraybuffer = new ArrayBuffer(bufferLength),
    bytes = new Uint8Array(arraybuffer);

    for (i = 0; i < len; i+=4) {
      encoded1 = lookup[base64.charCodeAt(i)];
      encoded2 = lookup[base64.charCodeAt(i+1)];
      encoded3 = lookup[base64.charCodeAt(i+2)];
      encoded4 = lookup[base64.charCodeAt(i+3)];

      bytes[p++] = (encoded1 << 2) | (encoded2 >> 4);
      bytes[p++] = ((encoded2 & 15) << 4) | (encoded3 >> 2);
      bytes[p++] = ((encoded3 & 3) << 6) | (encoded4 & 63);
    }

    return arraybuffer;
  };
})();


/***/ }),

/***/ "./node_modules/base64-js/index.js":
/*!*****************************************!*\
  !*** ./node_modules/base64-js/index.js ***!
  \*****************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


exports.byteLength = byteLength
exports.toByteArray = toByteArray
exports.fromByteArray = fromByteArray

var lookup = []
var revLookup = []
var Arr = typeof Uint8Array !== 'undefined' ? Uint8Array : Array

var code = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'
for (var i = 0, len = code.length; i < len; ++i) {
  lookup[i] = code[i]
  revLookup[code.charCodeAt(i)] = i
}

// Support decoding URL-safe base64 strings, as Node.js does.
// See: https://en.wikipedia.org/wiki/Base64#URL_applications
revLookup['-'.charCodeAt(0)] = 62
revLookup['_'.charCodeAt(0)] = 63

function getLens (b64) {
  var len = b64.length

  if (len % 4 > 0) {
    throw new Error('Invalid string. Length must be a multiple of 4')
  }

  // Trim off extra bytes after placeholder bytes are found
  // See: https://github.com/beatgammit/base64-js/issues/42
  var validLen = b64.indexOf('=')
  if (validLen === -1) validLen = len

  var placeHoldersLen = validLen === len
    ? 0
    : 4 - (validLen % 4)

  return [validLen, placeHoldersLen]
}

// base64 is 4/3 + up to two characters of the original data
function byteLength (b64) {
  var lens = getLens(b64)
  var validLen = lens[0]
  var placeHoldersLen = lens[1]
  return ((validLen + placeHoldersLen) * 3 / 4) - placeHoldersLen
}

function _byteLength (b64, validLen, placeHoldersLen) {
  return ((validLen + placeHoldersLen) * 3 / 4) - placeHoldersLen
}

function toByteArray (b64) {
  var tmp
  var lens = getLens(b64)
  var validLen = lens[0]
  var placeHoldersLen = lens[1]

  var arr = new Arr(_byteLength(b64, validLen, placeHoldersLen))

  var curByte = 0

  // if there are placeholders, only get up to the last complete 4 chars
  var len = placeHoldersLen > 0
    ? validLen - 4
    : validLen

  var i
  for (i = 0; i < len; i += 4) {
    tmp =
      (revLookup[b64.charCodeAt(i)] << 18) |
      (revLookup[b64.charCodeAt(i + 1)] << 12) |
      (revLookup[b64.charCodeAt(i + 2)] << 6) |
      revLookup[b64.charCodeAt(i + 3)]
    arr[curByte++] = (tmp >> 16) & 0xFF
    arr[curByte++] = (tmp >> 8) & 0xFF
    arr[curByte++] = tmp & 0xFF
  }

  if (placeHoldersLen === 2) {
    tmp =
      (revLookup[b64.charCodeAt(i)] << 2) |
      (revLookup[b64.charCodeAt(i + 1)] >> 4)
    arr[curByte++] = tmp & 0xFF
  }

  if (placeHoldersLen === 1) {
    tmp =
      (revLookup[b64.charCodeAt(i)] << 10) |
      (revLookup[b64.charCodeAt(i + 1)] << 4) |
      (revLookup[b64.charCodeAt(i + 2)] >> 2)
    arr[curByte++] = (tmp >> 8) & 0xFF
    arr[curByte++] = tmp & 0xFF
  }

  return arr
}

function tripletToBase64 (num) {
  return lookup[num >> 18 & 0x3F] +
    lookup[num >> 12 & 0x3F] +
    lookup[num >> 6 & 0x3F] +
    lookup[num & 0x3F]
}

function encodeChunk (uint8, start, end) {
  var tmp
  var output = []
  for (var i = start; i < end; i += 3) {
    tmp =
      ((uint8[i] << 16) & 0xFF0000) +
      ((uint8[i + 1] << 8) & 0xFF00) +
      (uint8[i + 2] & 0xFF)
    output.push(tripletToBase64(tmp))
  }
  return output.join('')
}

function fromByteArray (uint8) {
  var tmp
  var len = uint8.length
  var extraBytes = len % 3 // if we have 1 byte left, pad 2 bytes
  var parts = []
  var maxChunkLength = 16383 // must be multiple of 3

  // go through the array every three bytes, we'll deal with trailing stuff later
  for (var i = 0, len2 = len - extraBytes; i < len2; i += maxChunkLength) {
    parts.push(encodeChunk(
      uint8, i, (i + maxChunkLength) > len2 ? len2 : (i + maxChunkLength)
    ))
  }

  // pad the end with zeros, but make sure to not forget the extra bytes
  if (extraBytes === 1) {
    tmp = uint8[len - 1]
    parts.push(
      lookup[tmp >> 2] +
      lookup[(tmp << 4) & 0x3F] +
      '=='
    )
  } else if (extraBytes === 2) {
    tmp = (uint8[len - 2] << 8) + uint8[len - 1]
    parts.push(
      lookup[tmp >> 10] +
      lookup[(tmp >> 4) & 0x3F] +
      lookup[(tmp << 2) & 0x3F] +
      '='
    )
  }

  return parts.join('')
}


/***/ }),

/***/ "./node_modules/blob/index.js":
/*!************************************!*\
  !*** ./node_modules/blob/index.js ***!
  \************************************/
/*! no static exports found */
/***/ (function(module, exports) {

/**
 * Create a blob builder even when vendor prefixes exist
 */

var BlobBuilder = typeof BlobBuilder !== 'undefined' ? BlobBuilder :
  typeof WebKitBlobBuilder !== 'undefined' ? WebKitBlobBuilder :
  typeof MSBlobBuilder !== 'undefined' ? MSBlobBuilder :
  typeof MozBlobBuilder !== 'undefined' ? MozBlobBuilder : 
  false;

/**
 * Check if Blob constructor is supported
 */

var blobSupported = (function() {
  try {
    var a = new Blob(['hi']);
    return a.size === 2;
  } catch(e) {
    return false;
  }
})();

/**
 * Check if Blob constructor supports ArrayBufferViews
 * Fails in Safari 6, so we need to map to ArrayBuffers there.
 */

var blobSupportsArrayBufferView = blobSupported && (function() {
  try {
    var b = new Blob([new Uint8Array([1,2])]);
    return b.size === 2;
  } catch(e) {
    return false;
  }
})();

/**
 * Check if BlobBuilder is supported
 */

var blobBuilderSupported = BlobBuilder
  && BlobBuilder.prototype.append
  && BlobBuilder.prototype.getBlob;

/**
 * Helper function that maps ArrayBufferViews to ArrayBuffers
 * Used by BlobBuilder constructor and old browsers that didn't
 * support it in the Blob constructor.
 */

function mapArrayBufferViews(ary) {
  return ary.map(function(chunk) {
    if (chunk.buffer instanceof ArrayBuffer) {
      var buf = chunk.buffer;

      // if this is a subarray, make a copy so we only
      // include the subarray region from the underlying buffer
      if (chunk.byteLength !== buf.byteLength) {
        var copy = new Uint8Array(chunk.byteLength);
        copy.set(new Uint8Array(buf, chunk.byteOffset, chunk.byteLength));
        buf = copy.buffer;
      }

      return buf;
    }

    return chunk;
  });
}

function BlobBuilderConstructor(ary, options) {
  options = options || {};

  var bb = new BlobBuilder();
  mapArrayBufferViews(ary).forEach(function(part) {
    bb.append(part);
  });

  return (options.type) ? bb.getBlob(options.type) : bb.getBlob();
};

function BlobConstructor(ary, options) {
  return new Blob(mapArrayBufferViews(ary), options || {});
};

if (typeof Blob !== 'undefined') {
  BlobBuilderConstructor.prototype = Blob.prototype;
  BlobConstructor.prototype = Blob.prototype;
}

module.exports = (function() {
  if (blobSupported) {
    return blobSupportsArrayBufferView ? Blob : BlobConstructor;
  } else if (blobBuilderSupported) {
    return BlobBuilderConstructor;
  } else {
    return undefined;
  }
})();


/***/ }),

/***/ "./node_modules/bowser/src/bowser.js":
/*!*******************************************!*\
  !*** ./node_modules/bowser/src/bowser.js ***!
  \*******************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

/*!
 * Bowser - a browser detector
 * https://github.com/ded/bowser
 * MIT License | (c) Dustin Diaz 2015
 */

!function (root, name, definition) {
  if ( true && module.exports) module.exports = definition()
  else if (true) __webpack_require__(/*! !webpack amd define */ "./node_modules/webpack/buildin/amd-define.js")(name, definition)
  else {}
}(this, 'bowser', function () {
  /**
    * See useragents.js for examples of navigator.userAgent
    */

  var t = true

  function detect(ua) {

    function getFirstMatch(regex) {
      var match = ua.match(regex);
      return (match && match.length > 1 && match[1]) || '';
    }

    function getSecondMatch(regex) {
      var match = ua.match(regex);
      return (match && match.length > 1 && match[2]) || '';
    }

    var iosdevice = getFirstMatch(/(ipod|iphone|ipad)/i).toLowerCase()
      , likeAndroid = /like android/i.test(ua)
      , android = !likeAndroid && /android/i.test(ua)
      , nexusMobile = /nexus\s*[0-6]\s*/i.test(ua)
      , nexusTablet = !nexusMobile && /nexus\s*[0-9]+/i.test(ua)
      , chromeos = /CrOS/.test(ua)
      , silk = /silk/i.test(ua)
      , sailfish = /sailfish/i.test(ua)
      , tizen = /tizen/i.test(ua)
      , webos = /(web|hpw)(o|0)s/i.test(ua)
      , windowsphone = /windows phone/i.test(ua)
      , samsungBrowser = /SamsungBrowser/i.test(ua)
      , windows = !windowsphone && /windows/i.test(ua)
      , mac = !iosdevice && !silk && /macintosh/i.test(ua)
      , linux = !android && !sailfish && !tizen && !webos && /linux/i.test(ua)
      , edgeVersion = getSecondMatch(/edg([ea]|ios)\/(\d+(\.\d+)?)/i)
      , versionIdentifier = getFirstMatch(/version\/(\d+(\.\d+)?)/i)
      , tablet = /tablet/i.test(ua) && !/tablet pc/i.test(ua)
      , mobile = !tablet && /[^-]mobi/i.test(ua)
      , xbox = /xbox/i.test(ua)
      , result

    if (/opera/i.test(ua)) {
      //  an old Opera
      result = {
        name: 'Opera'
      , opera: t
      , version: versionIdentifier || getFirstMatch(/(?:opera|opr|opios)[\s\/](\d+(\.\d+)?)/i)
      }
    } else if (/opr\/|opios/i.test(ua)) {
      // a new Opera
      result = {
        name: 'Opera'
        , opera: t
        , version: getFirstMatch(/(?:opr|opios)[\s\/](\d+(\.\d+)?)/i) || versionIdentifier
      }
    }
    else if (/SamsungBrowser/i.test(ua)) {
      result = {
        name: 'Samsung Internet for Android'
        , samsungBrowser: t
        , version: versionIdentifier || getFirstMatch(/(?:SamsungBrowser)[\s\/](\d+(\.\d+)?)/i)
      }
    }
    else if (/Whale/i.test(ua)) {
      result = {
        name: 'NAVER Whale browser'
        , whale: t
        , version: getFirstMatch(/(?:whale)[\s\/](\d+(?:\.\d+)+)/i)
      }
    }
    else if (/MZBrowser/i.test(ua)) {
      result = {
        name: 'MZ Browser'
        , mzbrowser: t
        , version: getFirstMatch(/(?:MZBrowser)[\s\/](\d+(?:\.\d+)+)/i)
      }
    }
    else if (/coast/i.test(ua)) {
      result = {
        name: 'Opera Coast'
        , coast: t
        , version: versionIdentifier || getFirstMatch(/(?:coast)[\s\/](\d+(\.\d+)?)/i)
      }
    }
    else if (/focus/i.test(ua)) {
      result = {
        name: 'Focus'
        , focus: t
        , version: getFirstMatch(/(?:focus)[\s\/](\d+(?:\.\d+)+)/i)
      }
    }
    else if (/yabrowser/i.test(ua)) {
      result = {
        name: 'Yandex Browser'
      , yandexbrowser: t
      , version: versionIdentifier || getFirstMatch(/(?:yabrowser)[\s\/](\d+(\.\d+)?)/i)
      }
    }
    else if (/ucbrowser/i.test(ua)) {
      result = {
          name: 'UC Browser'
        , ucbrowser: t
        , version: getFirstMatch(/(?:ucbrowser)[\s\/](\d+(?:\.\d+)+)/i)
      }
    }
    else if (/mxios/i.test(ua)) {
      result = {
        name: 'Maxthon'
        , maxthon: t
        , version: getFirstMatch(/(?:mxios)[\s\/](\d+(?:\.\d+)+)/i)
      }
    }
    else if (/epiphany/i.test(ua)) {
      result = {
        name: 'Epiphany'
        , epiphany: t
        , version: getFirstMatch(/(?:epiphany)[\s\/](\d+(?:\.\d+)+)/i)
      }
    }
    else if (/puffin/i.test(ua)) {
      result = {
        name: 'Puffin'
        , puffin: t
        , version: getFirstMatch(/(?:puffin)[\s\/](\d+(?:\.\d+)?)/i)
      }
    }
    else if (/sleipnir/i.test(ua)) {
      result = {
        name: 'Sleipnir'
        , sleipnir: t
        , version: getFirstMatch(/(?:sleipnir)[\s\/](\d+(?:\.\d+)+)/i)
      }
    }
    else if (/k-meleon/i.test(ua)) {
      result = {
        name: 'K-Meleon'
        , kMeleon: t
        , version: getFirstMatch(/(?:k-meleon)[\s\/](\d+(?:\.\d+)+)/i)
      }
    }
    else if (windowsphone) {
      result = {
        name: 'Windows Phone'
      , osname: 'Windows Phone'
      , windowsphone: t
      }
      if (edgeVersion) {
        result.msedge = t
        result.version = edgeVersion
      }
      else {
        result.msie = t
        result.version = getFirstMatch(/iemobile\/(\d+(\.\d+)?)/i)
      }
    }
    else if (/msie|trident/i.test(ua)) {
      result = {
        name: 'Internet Explorer'
      , msie: t
      , version: getFirstMatch(/(?:msie |rv:)(\d+(\.\d+)?)/i)
      }
    } else if (chromeos) {
      result = {
        name: 'Chrome'
      , osname: 'Chrome OS'
      , chromeos: t
      , chromeBook: t
      , chrome: t
      , version: getFirstMatch(/(?:chrome|crios|crmo)\/(\d+(\.\d+)?)/i)
      }
    } else if (/edg([ea]|ios)/i.test(ua)) {
      result = {
        name: 'Microsoft Edge'
      , msedge: t
      , version: edgeVersion
      }
    }
    else if (/vivaldi/i.test(ua)) {
      result = {
        name: 'Vivaldi'
        , vivaldi: t
        , version: getFirstMatch(/vivaldi\/(\d+(\.\d+)?)/i) || versionIdentifier
      }
    }
    else if (sailfish) {
      result = {
        name: 'Sailfish'
      , osname: 'Sailfish OS'
      , sailfish: t
      , version: getFirstMatch(/sailfish\s?browser\/(\d+(\.\d+)?)/i)
      }
    }
    else if (/seamonkey\//i.test(ua)) {
      result = {
        name: 'SeaMonkey'
      , seamonkey: t
      , version: getFirstMatch(/seamonkey\/(\d+(\.\d+)?)/i)
      }
    }
    else if (/firefox|iceweasel|fxios/i.test(ua)) {
      result = {
        name: 'Firefox'
      , firefox: t
      , version: getFirstMatch(/(?:firefox|iceweasel|fxios)[ \/](\d+(\.\d+)?)/i)
      }
      if (/\((mobile|tablet);[^\)]*rv:[\d\.]+\)/i.test(ua)) {
        result.firefoxos = t
        result.osname = 'Firefox OS'
      }
    }
    else if (silk) {
      result =  {
        name: 'Amazon Silk'
      , silk: t
      , version : getFirstMatch(/silk\/(\d+(\.\d+)?)/i)
      }
    }
    else if (/phantom/i.test(ua)) {
      result = {
        name: 'PhantomJS'
      , phantom: t
      , version: getFirstMatch(/phantomjs\/(\d+(\.\d+)?)/i)
      }
    }
    else if (/slimerjs/i.test(ua)) {
      result = {
        name: 'SlimerJS'
        , slimer: t
        , version: getFirstMatch(/slimerjs\/(\d+(\.\d+)?)/i)
      }
    }
    else if (/blackberry|\bbb\d+/i.test(ua) || /rim\stablet/i.test(ua)) {
      result = {
        name: 'BlackBerry'
      , osname: 'BlackBerry OS'
      , blackberry: t
      , version: versionIdentifier || getFirstMatch(/blackberry[\d]+\/(\d+(\.\d+)?)/i)
      }
    }
    else if (webos) {
      result = {
        name: 'WebOS'
      , osname: 'WebOS'
      , webos: t
      , version: versionIdentifier || getFirstMatch(/w(?:eb)?osbrowser\/(\d+(\.\d+)?)/i)
      };
      /touchpad\//i.test(ua) && (result.touchpad = t)
    }
    else if (/bada/i.test(ua)) {
      result = {
        name: 'Bada'
      , osname: 'Bada'
      , bada: t
      , version: getFirstMatch(/dolfin\/(\d+(\.\d+)?)/i)
      };
    }
    else if (tizen) {
      result = {
        name: 'Tizen'
      , osname: 'Tizen'
      , tizen: t
      , version: getFirstMatch(/(?:tizen\s?)?browser\/(\d+(\.\d+)?)/i) || versionIdentifier
      };
    }
    else if (/qupzilla/i.test(ua)) {
      result = {
        name: 'QupZilla'
        , qupzilla: t
        , version: getFirstMatch(/(?:qupzilla)[\s\/](\d+(?:\.\d+)+)/i) || versionIdentifier
      }
    }
    else if (/chromium/i.test(ua)) {
      result = {
        name: 'Chromium'
        , chromium: t
        , version: getFirstMatch(/(?:chromium)[\s\/](\d+(?:\.\d+)?)/i) || versionIdentifier
      }
    }
    else if (/chrome|crios|crmo/i.test(ua)) {
      result = {
        name: 'Chrome'
        , chrome: t
        , version: getFirstMatch(/(?:chrome|crios|crmo)\/(\d+(\.\d+)?)/i)
      }
    }
    else if (android) {
      result = {
        name: 'Android'
        , version: versionIdentifier
      }
    }
    else if (/safari|applewebkit/i.test(ua)) {
      result = {
        name: 'Safari'
      , safari: t
      }
      if (versionIdentifier) {
        result.version = versionIdentifier
      }
    }
    else if (iosdevice) {
      result = {
        name : iosdevice == 'iphone' ? 'iPhone' : iosdevice == 'ipad' ? 'iPad' : 'iPod'
      }
      // WTF: version is not part of user agent in web apps
      if (versionIdentifier) {
        result.version = versionIdentifier
      }
    }
    else if(/googlebot/i.test(ua)) {
      result = {
        name: 'Googlebot'
      , googlebot: t
      , version: getFirstMatch(/googlebot\/(\d+(\.\d+))/i) || versionIdentifier
      }
    }
    else {
      result = {
        name: getFirstMatch(/^(.*)\/(.*) /),
        version: getSecondMatch(/^(.*)\/(.*) /)
     };
   }

    // set webkit or gecko flag for browsers based on these engines
    if (!result.msedge && /(apple)?webkit/i.test(ua)) {
      if (/(apple)?webkit\/537\.36/i.test(ua)) {
        result.name = result.name || "Blink"
        result.blink = t
      } else {
        result.name = result.name || "Webkit"
        result.webkit = t
      }
      if (!result.version && versionIdentifier) {
        result.version = versionIdentifier
      }
    } else if (!result.opera && /gecko\//i.test(ua)) {
      result.name = result.name || "Gecko"
      result.gecko = t
      result.version = result.version || getFirstMatch(/gecko\/(\d+(\.\d+)?)/i)
    }

    // set OS flags for platforms that have multiple browsers
    if (!result.windowsphone && (android || result.silk)) {
      result.android = t
      result.osname = 'Android'
    } else if (!result.windowsphone && iosdevice) {
      result[iosdevice] = t
      result.ios = t
      result.osname = 'iOS'
    } else if (mac) {
      result.mac = t
      result.osname = 'macOS'
    } else if (xbox) {
      result.xbox = t
      result.osname = 'Xbox'
    } else if (windows) {
      result.windows = t
      result.osname = 'Windows'
    } else if (linux) {
      result.linux = t
      result.osname = 'Linux'
    }

    function getWindowsVersion (s) {
      switch (s) {
        case 'NT': return 'NT'
        case 'XP': return 'XP'
        case 'NT 5.0': return '2000'
        case 'NT 5.1': return 'XP'
        case 'NT 5.2': return '2003'
        case 'NT 6.0': return 'Vista'
        case 'NT 6.1': return '7'
        case 'NT 6.2': return '8'
        case 'NT 6.3': return '8.1'
        case 'NT 10.0': return '10'
        default: return undefined
      }
    }

    // OS version extraction
    var osVersion = '';
    if (result.windows) {
      osVersion = getWindowsVersion(getFirstMatch(/Windows ((NT|XP)( \d\d?.\d)?)/i))
    } else if (result.windowsphone) {
      osVersion = getFirstMatch(/windows phone (?:os)?\s?(\d+(\.\d+)*)/i);
    } else if (result.mac) {
      osVersion = getFirstMatch(/Mac OS X (\d+([_\.\s]\d+)*)/i);
      osVersion = osVersion.replace(/[_\s]/g, '.');
    } else if (iosdevice) {
      osVersion = getFirstMatch(/os (\d+([_\s]\d+)*) like mac os x/i);
      osVersion = osVersion.replace(/[_\s]/g, '.');
    } else if (android) {
      osVersion = getFirstMatch(/android[ \/-](\d+(\.\d+)*)/i);
    } else if (result.webos) {
      osVersion = getFirstMatch(/(?:web|hpw)os\/(\d+(\.\d+)*)/i);
    } else if (result.blackberry) {
      osVersion = getFirstMatch(/rim\stablet\sos\s(\d+(\.\d+)*)/i);
    } else if (result.bada) {
      osVersion = getFirstMatch(/bada\/(\d+(\.\d+)*)/i);
    } else if (result.tizen) {
      osVersion = getFirstMatch(/tizen[\/\s](\d+(\.\d+)*)/i);
    }
    if (osVersion) {
      result.osversion = osVersion;
    }

    // device type extraction
    var osMajorVersion = !result.windows && osVersion.split('.')[0];
    if (
         tablet
      || nexusTablet
      || iosdevice == 'ipad'
      || (android && (osMajorVersion == 3 || (osMajorVersion >= 4 && !mobile)))
      || result.silk
    ) {
      result.tablet = t
    } else if (
         mobile
      || iosdevice == 'iphone'
      || iosdevice == 'ipod'
      || android
      || nexusMobile
      || result.blackberry
      || result.webos
      || result.bada
    ) {
      result.mobile = t
    }

    // Graded Browser Support
    // http://developer.yahoo.com/yui/articles/gbs
    if (result.msedge ||
        (result.msie && result.version >= 10) ||
        (result.yandexbrowser && result.version >= 15) ||
		    (result.vivaldi && result.version >= 1.0) ||
        (result.chrome && result.version >= 20) ||
        (result.samsungBrowser && result.version >= 4) ||
        (result.whale && compareVersions([result.version, '1.0']) === 1) ||
        (result.mzbrowser && compareVersions([result.version, '6.0']) === 1) ||
        (result.focus && compareVersions([result.version, '1.0']) === 1) ||
        (result.firefox && result.version >= 20.0) ||
        (result.safari && result.version >= 6) ||
        (result.opera && result.version >= 10.0) ||
        (result.ios && result.osversion && result.osversion.split(".")[0] >= 6) ||
        (result.blackberry && result.version >= 10.1)
        || (result.chromium && result.version >= 20)
        ) {
      result.a = t;
    }
    else if ((result.msie && result.version < 10) ||
        (result.chrome && result.version < 20) ||
        (result.firefox && result.version < 20.0) ||
        (result.safari && result.version < 6) ||
        (result.opera && result.version < 10.0) ||
        (result.ios && result.osversion && result.osversion.split(".")[0] < 6)
        || (result.chromium && result.version < 20)
        ) {
      result.c = t
    } else result.x = t

    return result
  }

  var bowser = detect(typeof navigator !== 'undefined' ? navigator.userAgent || '' : '')

  bowser.test = function (browserList) {
    for (var i = 0; i < browserList.length; ++i) {
      var browserItem = browserList[i];
      if (typeof browserItem=== 'string') {
        if (browserItem in bowser) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Get version precisions count
   *
   * @example
   *   getVersionPrecision("1.10.3") // 3
   *
   * @param  {string} version
   * @return {number}
   */
  function getVersionPrecision(version) {
    return version.split(".").length;
  }

  /**
   * Array::map polyfill
   *
   * @param  {Array} arr
   * @param  {Function} iterator
   * @return {Array}
   */
  function map(arr, iterator) {
    var result = [], i;
    if (Array.prototype.map) {
      return Array.prototype.map.call(arr, iterator);
    }
    for (i = 0; i < arr.length; i++) {
      result.push(iterator(arr[i]));
    }
    return result;
  }

  /**
   * Calculate browser version weight
   *
   * @example
   *   compareVersions(['1.10.2.1',  '1.8.2.1.90'])    // 1
   *   compareVersions(['1.010.2.1', '1.09.2.1.90']);  // 1
   *   compareVersions(['1.10.2.1',  '1.10.2.1']);     // 0
   *   compareVersions(['1.10.2.1',  '1.0800.2']);     // -1
   *
   * @param  {Array<String>} versions versions to compare
   * @return {Number} comparison result
   */
  function compareVersions(versions) {
    // 1) get common precision for both versions, for example for "10.0" and "9" it should be 2
    var precision = Math.max(getVersionPrecision(versions[0]), getVersionPrecision(versions[1]));
    var chunks = map(versions, function (version) {
      var delta = precision - getVersionPrecision(version);

      // 2) "9" -> "9.0" (for precision = 2)
      version = version + new Array(delta + 1).join(".0");

      // 3) "9.0" -> ["000000000"", "000000009"]
      return map(version.split("."), function (chunk) {
        return new Array(20 - chunk.length).join("0") + chunk;
      }).reverse();
    });

    // iterate in reverse order by reversed chunks array
    while (--precision >= 0) {
      // 4) compare: "000000009" > "000000010" = false (but "9" > "10" = true)
      if (chunks[0][precision] > chunks[1][precision]) {
        return 1;
      }
      else if (chunks[0][precision] === chunks[1][precision]) {
        if (precision === 0) {
          // all version chunks are same
          return 0;
        }
      }
      else {
        return -1;
      }
    }
  }

  /**
   * Check if browser is unsupported
   *
   * @example
   *   bowser.isUnsupportedBrowser({
   *     msie: "10",
   *     firefox: "23",
   *     chrome: "29",
   *     safari: "5.1",
   *     opera: "16",
   *     phantom: "534"
   *   });
   *
   * @param  {Object}  minVersions map of minimal version to browser
   * @param  {Boolean} [strictMode = false] flag to return false if browser wasn't found in map
   * @param  {String}  [ua] user agent string
   * @return {Boolean}
   */
  function isUnsupportedBrowser(minVersions, strictMode, ua) {
    var _bowser = bowser;

    // make strictMode param optional with ua param usage
    if (typeof strictMode === 'string') {
      ua = strictMode;
      strictMode = void(0);
    }

    if (strictMode === void(0)) {
      strictMode = false;
    }
    if (ua) {
      _bowser = detect(ua);
    }

    var version = "" + _bowser.version;
    for (var browser in minVersions) {
      if (minVersions.hasOwnProperty(browser)) {
        if (_bowser[browser]) {
          if (typeof minVersions[browser] !== 'string') {
            throw new Error('Browser version in the minVersion map should be a string: ' + browser + ': ' + String(minVersions));
          }

          // browser version and min supported version.
          return compareVersions([version, minVersions[browser]]) < 0;
        }
      }
    }

    return strictMode; // not found
  }

  /**
   * Check if browser is supported
   *
   * @param  {Object} minVersions map of minimal version to browser
   * @param  {Boolean} [strictMode = false] flag to return false if browser wasn't found in map
   * @param  {String}  [ua] user agent string
   * @return {Boolean}
   */
  function check(minVersions, strictMode, ua) {
    return !isUnsupportedBrowser(minVersions, strictMode, ua);
  }

  bowser.isUnsupportedBrowser = isUnsupportedBrowser;
  bowser.compareVersions = compareVersions;
  bowser.check = check;

  /*
   * Set our detect method to the main bowser object so we can
   * reuse it to test other user agents.
   * This is needed to implement future tests.
   */
  bowser._detect = detect;

  /*
   * Set our detect public method to the main bowser object
   * This is needed to implement bowser in server side
   */
  bowser.detect = detect;
  return bowser
});


/***/ }),

/***/ "./node_modules/buffer/index.js":
/*!**************************************!*\
  !*** ./node_modules/buffer/index.js ***!
  \**************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(global) {/*!
 * The buffer module from node.js, for the browser.
 *
 * @author   Feross Aboukhadijeh <feross@feross.org> <http://feross.org>
 * @license  MIT
 */
/* eslint-disable no-proto */



var base64 = __webpack_require__(/*! base64-js */ "./node_modules/base64-js/index.js")
var ieee754 = __webpack_require__(/*! ieee754 */ "./node_modules/ieee754/index.js")
var isArray = __webpack_require__(/*! isarray */ "./node_modules/buffer/node_modules/isarray/index.js")

exports.Buffer = Buffer
exports.SlowBuffer = SlowBuffer
exports.INSPECT_MAX_BYTES = 50

/**
 * If `Buffer.TYPED_ARRAY_SUPPORT`:
 *   === true    Use Uint8Array implementation (fastest)
 *   === false   Use Object implementation (most compatible, even IE6)
 *
 * Browsers that support typed arrays are IE 10+, Firefox 4+, Chrome 7+, Safari 5.1+,
 * Opera 11.6+, iOS 4.2+.
 *
 * Due to various browser bugs, sometimes the Object implementation will be used even
 * when the browser supports typed arrays.
 *
 * Note:
 *
 *   - Firefox 4-29 lacks support for adding new properties to `Uint8Array` instances,
 *     See: https://bugzilla.mozilla.org/show_bug.cgi?id=695438.
 *
 *   - Chrome 9-10 is missing the `TypedArray.prototype.subarray` function.
 *
 *   - IE10 has a broken `TypedArray.prototype.subarray` function which returns arrays of
 *     incorrect length in some situations.

 * We detect these buggy browsers and set `Buffer.TYPED_ARRAY_SUPPORT` to `false` so they
 * get the Object implementation, which is slower but behaves correctly.
 */
Buffer.TYPED_ARRAY_SUPPORT = global.TYPED_ARRAY_SUPPORT !== undefined
  ? global.TYPED_ARRAY_SUPPORT
  : typedArraySupport()

/*
 * Export kMaxLength after typed array support is determined.
 */
exports.kMaxLength = kMaxLength()

function typedArraySupport () {
  try {
    var arr = new Uint8Array(1)
    arr.__proto__ = {__proto__: Uint8Array.prototype, foo: function () { return 42 }}
    return arr.foo() === 42 && // typed array instances can be augmented
        typeof arr.subarray === 'function' && // chrome 9-10 lack `subarray`
        arr.subarray(1, 1).byteLength === 0 // ie10 has broken `subarray`
  } catch (e) {
    return false
  }
}

function kMaxLength () {
  return Buffer.TYPED_ARRAY_SUPPORT
    ? 0x7fffffff
    : 0x3fffffff
}

function createBuffer (that, length) {
  if (kMaxLength() < length) {
    throw new RangeError('Invalid typed array length')
  }
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    // Return an augmented `Uint8Array` instance, for best performance
    that = new Uint8Array(length)
    that.__proto__ = Buffer.prototype
  } else {
    // Fallback: Return an object instance of the Buffer class
    if (that === null) {
      that = new Buffer(length)
    }
    that.length = length
  }

  return that
}

/**
 * The Buffer constructor returns instances of `Uint8Array` that have their
 * prototype changed to `Buffer.prototype`. Furthermore, `Buffer` is a subclass of
 * `Uint8Array`, so the returned instances will have all the node `Buffer` methods
 * and the `Uint8Array` methods. Square bracket notation works as expected -- it
 * returns a single octet.
 *
 * The `Uint8Array` prototype remains unmodified.
 */

function Buffer (arg, encodingOrOffset, length) {
  if (!Buffer.TYPED_ARRAY_SUPPORT && !(this instanceof Buffer)) {
    return new Buffer(arg, encodingOrOffset, length)
  }

  // Common case.
  if (typeof arg === 'number') {
    if (typeof encodingOrOffset === 'string') {
      throw new Error(
        'If encoding is specified then the first argument must be a string'
      )
    }
    return allocUnsafe(this, arg)
  }
  return from(this, arg, encodingOrOffset, length)
}

Buffer.poolSize = 8192 // not used by this implementation

// TODO: Legacy, not needed anymore. Remove in next major version.
Buffer._augment = function (arr) {
  arr.__proto__ = Buffer.prototype
  return arr
}

function from (that, value, encodingOrOffset, length) {
  if (typeof value === 'number') {
    throw new TypeError('"value" argument must not be a number')
  }

  if (typeof ArrayBuffer !== 'undefined' && value instanceof ArrayBuffer) {
    return fromArrayBuffer(that, value, encodingOrOffset, length)
  }

  if (typeof value === 'string') {
    return fromString(that, value, encodingOrOffset)
  }

  return fromObject(that, value)
}

/**
 * Functionally equivalent to Buffer(arg, encoding) but throws a TypeError
 * if value is a number.
 * Buffer.from(str[, encoding])
 * Buffer.from(array)
 * Buffer.from(buffer)
 * Buffer.from(arrayBuffer[, byteOffset[, length]])
 **/
Buffer.from = function (value, encodingOrOffset, length) {
  return from(null, value, encodingOrOffset, length)
}

if (Buffer.TYPED_ARRAY_SUPPORT) {
  Buffer.prototype.__proto__ = Uint8Array.prototype
  Buffer.__proto__ = Uint8Array
  if (typeof Symbol !== 'undefined' && Symbol.species &&
      Buffer[Symbol.species] === Buffer) {
    // Fix subarray() in ES2016. See: https://github.com/feross/buffer/pull/97
    Object.defineProperty(Buffer, Symbol.species, {
      value: null,
      configurable: true
    })
  }
}

function assertSize (size) {
  if (typeof size !== 'number') {
    throw new TypeError('"size" argument must be a number')
  } else if (size < 0) {
    throw new RangeError('"size" argument must not be negative')
  }
}

function alloc (that, size, fill, encoding) {
  assertSize(size)
  if (size <= 0) {
    return createBuffer(that, size)
  }
  if (fill !== undefined) {
    // Only pay attention to encoding if it's a string. This
    // prevents accidentally sending in a number that would
    // be interpretted as a start offset.
    return typeof encoding === 'string'
      ? createBuffer(that, size).fill(fill, encoding)
      : createBuffer(that, size).fill(fill)
  }
  return createBuffer(that, size)
}

/**
 * Creates a new filled Buffer instance.
 * alloc(size[, fill[, encoding]])
 **/
Buffer.alloc = function (size, fill, encoding) {
  return alloc(null, size, fill, encoding)
}

function allocUnsafe (that, size) {
  assertSize(size)
  that = createBuffer(that, size < 0 ? 0 : checked(size) | 0)
  if (!Buffer.TYPED_ARRAY_SUPPORT) {
    for (var i = 0; i < size; ++i) {
      that[i] = 0
    }
  }
  return that
}

/**
 * Equivalent to Buffer(num), by default creates a non-zero-filled Buffer instance.
 * */
Buffer.allocUnsafe = function (size) {
  return allocUnsafe(null, size)
}
/**
 * Equivalent to SlowBuffer(num), by default creates a non-zero-filled Buffer instance.
 */
Buffer.allocUnsafeSlow = function (size) {
  return allocUnsafe(null, size)
}

function fromString (that, string, encoding) {
  if (typeof encoding !== 'string' || encoding === '') {
    encoding = 'utf8'
  }

  if (!Buffer.isEncoding(encoding)) {
    throw new TypeError('"encoding" must be a valid string encoding')
  }

  var length = byteLength(string, encoding) | 0
  that = createBuffer(that, length)

  var actual = that.write(string, encoding)

  if (actual !== length) {
    // Writing a hex string, for example, that contains invalid characters will
    // cause everything after the first invalid character to be ignored. (e.g.
    // 'abxxcd' will be treated as 'ab')
    that = that.slice(0, actual)
  }

  return that
}

function fromArrayLike (that, array) {
  var length = array.length < 0 ? 0 : checked(array.length) | 0
  that = createBuffer(that, length)
  for (var i = 0; i < length; i += 1) {
    that[i] = array[i] & 255
  }
  return that
}

function fromArrayBuffer (that, array, byteOffset, length) {
  array.byteLength // this throws if `array` is not a valid ArrayBuffer

  if (byteOffset < 0 || array.byteLength < byteOffset) {
    throw new RangeError('\'offset\' is out of bounds')
  }

  if (array.byteLength < byteOffset + (length || 0)) {
    throw new RangeError('\'length\' is out of bounds')
  }

  if (byteOffset === undefined && length === undefined) {
    array = new Uint8Array(array)
  } else if (length === undefined) {
    array = new Uint8Array(array, byteOffset)
  } else {
    array = new Uint8Array(array, byteOffset, length)
  }

  if (Buffer.TYPED_ARRAY_SUPPORT) {
    // Return an augmented `Uint8Array` instance, for best performance
    that = array
    that.__proto__ = Buffer.prototype
  } else {
    // Fallback: Return an object instance of the Buffer class
    that = fromArrayLike(that, array)
  }
  return that
}

function fromObject (that, obj) {
  if (Buffer.isBuffer(obj)) {
    var len = checked(obj.length) | 0
    that = createBuffer(that, len)

    if (that.length === 0) {
      return that
    }

    obj.copy(that, 0, 0, len)
    return that
  }

  if (obj) {
    if ((typeof ArrayBuffer !== 'undefined' &&
        obj.buffer instanceof ArrayBuffer) || 'length' in obj) {
      if (typeof obj.length !== 'number' || isnan(obj.length)) {
        return createBuffer(that, 0)
      }
      return fromArrayLike(that, obj)
    }

    if (obj.type === 'Buffer' && isArray(obj.data)) {
      return fromArrayLike(that, obj.data)
    }
  }

  throw new TypeError('First argument must be a string, Buffer, ArrayBuffer, Array, or array-like object.')
}

function checked (length) {
  // Note: cannot use `length < kMaxLength()` here because that fails when
  // length is NaN (which is otherwise coerced to zero.)
  if (length >= kMaxLength()) {
    throw new RangeError('Attempt to allocate Buffer larger than maximum ' +
                         'size: 0x' + kMaxLength().toString(16) + ' bytes')
  }
  return length | 0
}

function SlowBuffer (length) {
  if (+length != length) { // eslint-disable-line eqeqeq
    length = 0
  }
  return Buffer.alloc(+length)
}

Buffer.isBuffer = function isBuffer (b) {
  return !!(b != null && b._isBuffer)
}

Buffer.compare = function compare (a, b) {
  if (!Buffer.isBuffer(a) || !Buffer.isBuffer(b)) {
    throw new TypeError('Arguments must be Buffers')
  }

  if (a === b) return 0

  var x = a.length
  var y = b.length

  for (var i = 0, len = Math.min(x, y); i < len; ++i) {
    if (a[i] !== b[i]) {
      x = a[i]
      y = b[i]
      break
    }
  }

  if (x < y) return -1
  if (y < x) return 1
  return 0
}

Buffer.isEncoding = function isEncoding (encoding) {
  switch (String(encoding).toLowerCase()) {
    case 'hex':
    case 'utf8':
    case 'utf-8':
    case 'ascii':
    case 'latin1':
    case 'binary':
    case 'base64':
    case 'ucs2':
    case 'ucs-2':
    case 'utf16le':
    case 'utf-16le':
      return true
    default:
      return false
  }
}

Buffer.concat = function concat (list, length) {
  if (!isArray(list)) {
    throw new TypeError('"list" argument must be an Array of Buffers')
  }

  if (list.length === 0) {
    return Buffer.alloc(0)
  }

  var i
  if (length === undefined) {
    length = 0
    for (i = 0; i < list.length; ++i) {
      length += list[i].length
    }
  }

  var buffer = Buffer.allocUnsafe(length)
  var pos = 0
  for (i = 0; i < list.length; ++i) {
    var buf = list[i]
    if (!Buffer.isBuffer(buf)) {
      throw new TypeError('"list" argument must be an Array of Buffers')
    }
    buf.copy(buffer, pos)
    pos += buf.length
  }
  return buffer
}

function byteLength (string, encoding) {
  if (Buffer.isBuffer(string)) {
    return string.length
  }
  if (typeof ArrayBuffer !== 'undefined' && typeof ArrayBuffer.isView === 'function' &&
      (ArrayBuffer.isView(string) || string instanceof ArrayBuffer)) {
    return string.byteLength
  }
  if (typeof string !== 'string') {
    string = '' + string
  }

  var len = string.length
  if (len === 0) return 0

  // Use a for loop to avoid recursion
  var loweredCase = false
  for (;;) {
    switch (encoding) {
      case 'ascii':
      case 'latin1':
      case 'binary':
        return len
      case 'utf8':
      case 'utf-8':
      case undefined:
        return utf8ToBytes(string).length
      case 'ucs2':
      case 'ucs-2':
      case 'utf16le':
      case 'utf-16le':
        return len * 2
      case 'hex':
        return len >>> 1
      case 'base64':
        return base64ToBytes(string).length
      default:
        if (loweredCase) return utf8ToBytes(string).length // assume utf8
        encoding = ('' + encoding).toLowerCase()
        loweredCase = true
    }
  }
}
Buffer.byteLength = byteLength

function slowToString (encoding, start, end) {
  var loweredCase = false

  // No need to verify that "this.length <= MAX_UINT32" since it's a read-only
  // property of a typed array.

  // This behaves neither like String nor Uint8Array in that we set start/end
  // to their upper/lower bounds if the value passed is out of range.
  // undefined is handled specially as per ECMA-262 6th Edition,
  // Section 13.3.3.7 Runtime Semantics: KeyedBindingInitialization.
  if (start === undefined || start < 0) {
    start = 0
  }
  // Return early if start > this.length. Done here to prevent potential uint32
  // coercion fail below.
  if (start > this.length) {
    return ''
  }

  if (end === undefined || end > this.length) {
    end = this.length
  }

  if (end <= 0) {
    return ''
  }

  // Force coersion to uint32. This will also coerce falsey/NaN values to 0.
  end >>>= 0
  start >>>= 0

  if (end <= start) {
    return ''
  }

  if (!encoding) encoding = 'utf8'

  while (true) {
    switch (encoding) {
      case 'hex':
        return hexSlice(this, start, end)

      case 'utf8':
      case 'utf-8':
        return utf8Slice(this, start, end)

      case 'ascii':
        return asciiSlice(this, start, end)

      case 'latin1':
      case 'binary':
        return latin1Slice(this, start, end)

      case 'base64':
        return base64Slice(this, start, end)

      case 'ucs2':
      case 'ucs-2':
      case 'utf16le':
      case 'utf-16le':
        return utf16leSlice(this, start, end)

      default:
        if (loweredCase) throw new TypeError('Unknown encoding: ' + encoding)
        encoding = (encoding + '').toLowerCase()
        loweredCase = true
    }
  }
}

// The property is used by `Buffer.isBuffer` and `is-buffer` (in Safari 5-7) to detect
// Buffer instances.
Buffer.prototype._isBuffer = true

function swap (b, n, m) {
  var i = b[n]
  b[n] = b[m]
  b[m] = i
}

Buffer.prototype.swap16 = function swap16 () {
  var len = this.length
  if (len % 2 !== 0) {
    throw new RangeError('Buffer size must be a multiple of 16-bits')
  }
  for (var i = 0; i < len; i += 2) {
    swap(this, i, i + 1)
  }
  return this
}

Buffer.prototype.swap32 = function swap32 () {
  var len = this.length
  if (len % 4 !== 0) {
    throw new RangeError('Buffer size must be a multiple of 32-bits')
  }
  for (var i = 0; i < len; i += 4) {
    swap(this, i, i + 3)
    swap(this, i + 1, i + 2)
  }
  return this
}

Buffer.prototype.swap64 = function swap64 () {
  var len = this.length
  if (len % 8 !== 0) {
    throw new RangeError('Buffer size must be a multiple of 64-bits')
  }
  for (var i = 0; i < len; i += 8) {
    swap(this, i, i + 7)
    swap(this, i + 1, i + 6)
    swap(this, i + 2, i + 5)
    swap(this, i + 3, i + 4)
  }
  return this
}

Buffer.prototype.toString = function toString () {
  var length = this.length | 0
  if (length === 0) return ''
  if (arguments.length === 0) return utf8Slice(this, 0, length)
  return slowToString.apply(this, arguments)
}

Buffer.prototype.equals = function equals (b) {
  if (!Buffer.isBuffer(b)) throw new TypeError('Argument must be a Buffer')
  if (this === b) return true
  return Buffer.compare(this, b) === 0
}

Buffer.prototype.inspect = function inspect () {
  var str = ''
  var max = exports.INSPECT_MAX_BYTES
  if (this.length > 0) {
    str = this.toString('hex', 0, max).match(/.{2}/g).join(' ')
    if (this.length > max) str += ' ... '
  }
  return '<Buffer ' + str + '>'
}

Buffer.prototype.compare = function compare (target, start, end, thisStart, thisEnd) {
  if (!Buffer.isBuffer(target)) {
    throw new TypeError('Argument must be a Buffer')
  }

  if (start === undefined) {
    start = 0
  }
  if (end === undefined) {
    end = target ? target.length : 0
  }
  if (thisStart === undefined) {
    thisStart = 0
  }
  if (thisEnd === undefined) {
    thisEnd = this.length
  }

  if (start < 0 || end > target.length || thisStart < 0 || thisEnd > this.length) {
    throw new RangeError('out of range index')
  }

  if (thisStart >= thisEnd && start >= end) {
    return 0
  }
  if (thisStart >= thisEnd) {
    return -1
  }
  if (start >= end) {
    return 1
  }

  start >>>= 0
  end >>>= 0
  thisStart >>>= 0
  thisEnd >>>= 0

  if (this === target) return 0

  var x = thisEnd - thisStart
  var y = end - start
  var len = Math.min(x, y)

  var thisCopy = this.slice(thisStart, thisEnd)
  var targetCopy = target.slice(start, end)

  for (var i = 0; i < len; ++i) {
    if (thisCopy[i] !== targetCopy[i]) {
      x = thisCopy[i]
      y = targetCopy[i]
      break
    }
  }

  if (x < y) return -1
  if (y < x) return 1
  return 0
}

// Finds either the first index of `val` in `buffer` at offset >= `byteOffset`,
// OR the last index of `val` in `buffer` at offset <= `byteOffset`.
//
// Arguments:
// - buffer - a Buffer to search
// - val - a string, Buffer, or number
// - byteOffset - an index into `buffer`; will be clamped to an int32
// - encoding - an optional encoding, relevant is val is a string
// - dir - true for indexOf, false for lastIndexOf
function bidirectionalIndexOf (buffer, val, byteOffset, encoding, dir) {
  // Empty buffer means no match
  if (buffer.length === 0) return -1

  // Normalize byteOffset
  if (typeof byteOffset === 'string') {
    encoding = byteOffset
    byteOffset = 0
  } else if (byteOffset > 0x7fffffff) {
    byteOffset = 0x7fffffff
  } else if (byteOffset < -0x80000000) {
    byteOffset = -0x80000000
  }
  byteOffset = +byteOffset  // Coerce to Number.
  if (isNaN(byteOffset)) {
    // byteOffset: it it's undefined, null, NaN, "foo", etc, search whole buffer
    byteOffset = dir ? 0 : (buffer.length - 1)
  }

  // Normalize byteOffset: negative offsets start from the end of the buffer
  if (byteOffset < 0) byteOffset = buffer.length + byteOffset
  if (byteOffset >= buffer.length) {
    if (dir) return -1
    else byteOffset = buffer.length - 1
  } else if (byteOffset < 0) {
    if (dir) byteOffset = 0
    else return -1
  }

  // Normalize val
  if (typeof val === 'string') {
    val = Buffer.from(val, encoding)
  }

  // Finally, search either indexOf (if dir is true) or lastIndexOf
  if (Buffer.isBuffer(val)) {
    // Special case: looking for empty string/buffer always fails
    if (val.length === 0) {
      return -1
    }
    return arrayIndexOf(buffer, val, byteOffset, encoding, dir)
  } else if (typeof val === 'number') {
    val = val & 0xFF // Search for a byte value [0-255]
    if (Buffer.TYPED_ARRAY_SUPPORT &&
        typeof Uint8Array.prototype.indexOf === 'function') {
      if (dir) {
        return Uint8Array.prototype.indexOf.call(buffer, val, byteOffset)
      } else {
        return Uint8Array.prototype.lastIndexOf.call(buffer, val, byteOffset)
      }
    }
    return arrayIndexOf(buffer, [ val ], byteOffset, encoding, dir)
  }

  throw new TypeError('val must be string, number or Buffer')
}

function arrayIndexOf (arr, val, byteOffset, encoding, dir) {
  var indexSize = 1
  var arrLength = arr.length
  var valLength = val.length

  if (encoding !== undefined) {
    encoding = String(encoding).toLowerCase()
    if (encoding === 'ucs2' || encoding === 'ucs-2' ||
        encoding === 'utf16le' || encoding === 'utf-16le') {
      if (arr.length < 2 || val.length < 2) {
        return -1
      }
      indexSize = 2
      arrLength /= 2
      valLength /= 2
      byteOffset /= 2
    }
  }

  function read (buf, i) {
    if (indexSize === 1) {
      return buf[i]
    } else {
      return buf.readUInt16BE(i * indexSize)
    }
  }

  var i
  if (dir) {
    var foundIndex = -1
    for (i = byteOffset; i < arrLength; i++) {
      if (read(arr, i) === read(val, foundIndex === -1 ? 0 : i - foundIndex)) {
        if (foundIndex === -1) foundIndex = i
        if (i - foundIndex + 1 === valLength) return foundIndex * indexSize
      } else {
        if (foundIndex !== -1) i -= i - foundIndex
        foundIndex = -1
      }
    }
  } else {
    if (byteOffset + valLength > arrLength) byteOffset = arrLength - valLength
    for (i = byteOffset; i >= 0; i--) {
      var found = true
      for (var j = 0; j < valLength; j++) {
        if (read(arr, i + j) !== read(val, j)) {
          found = false
          break
        }
      }
      if (found) return i
    }
  }

  return -1
}

Buffer.prototype.includes = function includes (val, byteOffset, encoding) {
  return this.indexOf(val, byteOffset, encoding) !== -1
}

Buffer.prototype.indexOf = function indexOf (val, byteOffset, encoding) {
  return bidirectionalIndexOf(this, val, byteOffset, encoding, true)
}

Buffer.prototype.lastIndexOf = function lastIndexOf (val, byteOffset, encoding) {
  return bidirectionalIndexOf(this, val, byteOffset, encoding, false)
}

function hexWrite (buf, string, offset, length) {
  offset = Number(offset) || 0
  var remaining = buf.length - offset
  if (!length) {
    length = remaining
  } else {
    length = Number(length)
    if (length > remaining) {
      length = remaining
    }
  }

  // must be an even number of digits
  var strLen = string.length
  if (strLen % 2 !== 0) throw new TypeError('Invalid hex string')

  if (length > strLen / 2) {
    length = strLen / 2
  }
  for (var i = 0; i < length; ++i) {
    var parsed = parseInt(string.substr(i * 2, 2), 16)
    if (isNaN(parsed)) return i
    buf[offset + i] = parsed
  }
  return i
}

function utf8Write (buf, string, offset, length) {
  return blitBuffer(utf8ToBytes(string, buf.length - offset), buf, offset, length)
}

function asciiWrite (buf, string, offset, length) {
  return blitBuffer(asciiToBytes(string), buf, offset, length)
}

function latin1Write (buf, string, offset, length) {
  return asciiWrite(buf, string, offset, length)
}

function base64Write (buf, string, offset, length) {
  return blitBuffer(base64ToBytes(string), buf, offset, length)
}

function ucs2Write (buf, string, offset, length) {
  return blitBuffer(utf16leToBytes(string, buf.length - offset), buf, offset, length)
}

Buffer.prototype.write = function write (string, offset, length, encoding) {
  // Buffer#write(string)
  if (offset === undefined) {
    encoding = 'utf8'
    length = this.length
    offset = 0
  // Buffer#write(string, encoding)
  } else if (length === undefined && typeof offset === 'string') {
    encoding = offset
    length = this.length
    offset = 0
  // Buffer#write(string, offset[, length][, encoding])
  } else if (isFinite(offset)) {
    offset = offset | 0
    if (isFinite(length)) {
      length = length | 0
      if (encoding === undefined) encoding = 'utf8'
    } else {
      encoding = length
      length = undefined
    }
  // legacy write(string, encoding, offset, length) - remove in v0.13
  } else {
    throw new Error(
      'Buffer.write(string, encoding, offset[, length]) is no longer supported'
    )
  }

  var remaining = this.length - offset
  if (length === undefined || length > remaining) length = remaining

  if ((string.length > 0 && (length < 0 || offset < 0)) || offset > this.length) {
    throw new RangeError('Attempt to write outside buffer bounds')
  }

  if (!encoding) encoding = 'utf8'

  var loweredCase = false
  for (;;) {
    switch (encoding) {
      case 'hex':
        return hexWrite(this, string, offset, length)

      case 'utf8':
      case 'utf-8':
        return utf8Write(this, string, offset, length)

      case 'ascii':
        return asciiWrite(this, string, offset, length)

      case 'latin1':
      case 'binary':
        return latin1Write(this, string, offset, length)

      case 'base64':
        // Warning: maxLength not taken into account in base64Write
        return base64Write(this, string, offset, length)

      case 'ucs2':
      case 'ucs-2':
      case 'utf16le':
      case 'utf-16le':
        return ucs2Write(this, string, offset, length)

      default:
        if (loweredCase) throw new TypeError('Unknown encoding: ' + encoding)
        encoding = ('' + encoding).toLowerCase()
        loweredCase = true
    }
  }
}

Buffer.prototype.toJSON = function toJSON () {
  return {
    type: 'Buffer',
    data: Array.prototype.slice.call(this._arr || this, 0)
  }
}

function base64Slice (buf, start, end) {
  if (start === 0 && end === buf.length) {
    return base64.fromByteArray(buf)
  } else {
    return base64.fromByteArray(buf.slice(start, end))
  }
}

function utf8Slice (buf, start, end) {
  end = Math.min(buf.length, end)
  var res = []

  var i = start
  while (i < end) {
    var firstByte = buf[i]
    var codePoint = null
    var bytesPerSequence = (firstByte > 0xEF) ? 4
      : (firstByte > 0xDF) ? 3
      : (firstByte > 0xBF) ? 2
      : 1

    if (i + bytesPerSequence <= end) {
      var secondByte, thirdByte, fourthByte, tempCodePoint

      switch (bytesPerSequence) {
        case 1:
          if (firstByte < 0x80) {
            codePoint = firstByte
          }
          break
        case 2:
          secondByte = buf[i + 1]
          if ((secondByte & 0xC0) === 0x80) {
            tempCodePoint = (firstByte & 0x1F) << 0x6 | (secondByte & 0x3F)
            if (tempCodePoint > 0x7F) {
              codePoint = tempCodePoint
            }
          }
          break
        case 3:
          secondByte = buf[i + 1]
          thirdByte = buf[i + 2]
          if ((secondByte & 0xC0) === 0x80 && (thirdByte & 0xC0) === 0x80) {
            tempCodePoint = (firstByte & 0xF) << 0xC | (secondByte & 0x3F) << 0x6 | (thirdByte & 0x3F)
            if (tempCodePoint > 0x7FF && (tempCodePoint < 0xD800 || tempCodePoint > 0xDFFF)) {
              codePoint = tempCodePoint
            }
          }
          break
        case 4:
          secondByte = buf[i + 1]
          thirdByte = buf[i + 2]
          fourthByte = buf[i + 3]
          if ((secondByte & 0xC0) === 0x80 && (thirdByte & 0xC0) === 0x80 && (fourthByte & 0xC0) === 0x80) {
            tempCodePoint = (firstByte & 0xF) << 0x12 | (secondByte & 0x3F) << 0xC | (thirdByte & 0x3F) << 0x6 | (fourthByte & 0x3F)
            if (tempCodePoint > 0xFFFF && tempCodePoint < 0x110000) {
              codePoint = tempCodePoint
            }
          }
      }
    }

    if (codePoint === null) {
      // we did not generate a valid codePoint so insert a
      // replacement char (U+FFFD) and advance only 1 byte
      codePoint = 0xFFFD
      bytesPerSequence = 1
    } else if (codePoint > 0xFFFF) {
      // encode to utf16 (surrogate pair dance)
      codePoint -= 0x10000
      res.push(codePoint >>> 10 & 0x3FF | 0xD800)
      codePoint = 0xDC00 | codePoint & 0x3FF
    }

    res.push(codePoint)
    i += bytesPerSequence
  }

  return decodeCodePointsArray(res)
}

// Based on http://stackoverflow.com/a/22747272/680742, the browser with
// the lowest limit is Chrome, with 0x10000 args.
// We go 1 magnitude less, for safety
var MAX_ARGUMENTS_LENGTH = 0x1000

function decodeCodePointsArray (codePoints) {
  var len = codePoints.length
  if (len <= MAX_ARGUMENTS_LENGTH) {
    return String.fromCharCode.apply(String, codePoints) // avoid extra slice()
  }

  // Decode in chunks to avoid "call stack size exceeded".
  var res = ''
  var i = 0
  while (i < len) {
    res += String.fromCharCode.apply(
      String,
      codePoints.slice(i, i += MAX_ARGUMENTS_LENGTH)
    )
  }
  return res
}

function asciiSlice (buf, start, end) {
  var ret = ''
  end = Math.min(buf.length, end)

  for (var i = start; i < end; ++i) {
    ret += String.fromCharCode(buf[i] & 0x7F)
  }
  return ret
}

function latin1Slice (buf, start, end) {
  var ret = ''
  end = Math.min(buf.length, end)

  for (var i = start; i < end; ++i) {
    ret += String.fromCharCode(buf[i])
  }
  return ret
}

function hexSlice (buf, start, end) {
  var len = buf.length

  if (!start || start < 0) start = 0
  if (!end || end < 0 || end > len) end = len

  var out = ''
  for (var i = start; i < end; ++i) {
    out += toHex(buf[i])
  }
  return out
}

function utf16leSlice (buf, start, end) {
  var bytes = buf.slice(start, end)
  var res = ''
  for (var i = 0; i < bytes.length; i += 2) {
    res += String.fromCharCode(bytes[i] + bytes[i + 1] * 256)
  }
  return res
}

Buffer.prototype.slice = function slice (start, end) {
  var len = this.length
  start = ~~start
  end = end === undefined ? len : ~~end

  if (start < 0) {
    start += len
    if (start < 0) start = 0
  } else if (start > len) {
    start = len
  }

  if (end < 0) {
    end += len
    if (end < 0) end = 0
  } else if (end > len) {
    end = len
  }

  if (end < start) end = start

  var newBuf
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    newBuf = this.subarray(start, end)
    newBuf.__proto__ = Buffer.prototype
  } else {
    var sliceLen = end - start
    newBuf = new Buffer(sliceLen, undefined)
    for (var i = 0; i < sliceLen; ++i) {
      newBuf[i] = this[i + start]
    }
  }

  return newBuf
}

/*
 * Need to make sure that buffer isn't trying to write out of bounds.
 */
function checkOffset (offset, ext, length) {
  if ((offset % 1) !== 0 || offset < 0) throw new RangeError('offset is not uint')
  if (offset + ext > length) throw new RangeError('Trying to access beyond buffer length')
}

Buffer.prototype.readUIntLE = function readUIntLE (offset, byteLength, noAssert) {
  offset = offset | 0
  byteLength = byteLength | 0
  if (!noAssert) checkOffset(offset, byteLength, this.length)

  var val = this[offset]
  var mul = 1
  var i = 0
  while (++i < byteLength && (mul *= 0x100)) {
    val += this[offset + i] * mul
  }

  return val
}

Buffer.prototype.readUIntBE = function readUIntBE (offset, byteLength, noAssert) {
  offset = offset | 0
  byteLength = byteLength | 0
  if (!noAssert) {
    checkOffset(offset, byteLength, this.length)
  }

  var val = this[offset + --byteLength]
  var mul = 1
  while (byteLength > 0 && (mul *= 0x100)) {
    val += this[offset + --byteLength] * mul
  }

  return val
}

Buffer.prototype.readUInt8 = function readUInt8 (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 1, this.length)
  return this[offset]
}

Buffer.prototype.readUInt16LE = function readUInt16LE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 2, this.length)
  return this[offset] | (this[offset + 1] << 8)
}

Buffer.prototype.readUInt16BE = function readUInt16BE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 2, this.length)
  return (this[offset] << 8) | this[offset + 1]
}

Buffer.prototype.readUInt32LE = function readUInt32LE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 4, this.length)

  return ((this[offset]) |
      (this[offset + 1] << 8) |
      (this[offset + 2] << 16)) +
      (this[offset + 3] * 0x1000000)
}

Buffer.prototype.readUInt32BE = function readUInt32BE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 4, this.length)

  return (this[offset] * 0x1000000) +
    ((this[offset + 1] << 16) |
    (this[offset + 2] << 8) |
    this[offset + 3])
}

Buffer.prototype.readIntLE = function readIntLE (offset, byteLength, noAssert) {
  offset = offset | 0
  byteLength = byteLength | 0
  if (!noAssert) checkOffset(offset, byteLength, this.length)

  var val = this[offset]
  var mul = 1
  var i = 0
  while (++i < byteLength && (mul *= 0x100)) {
    val += this[offset + i] * mul
  }
  mul *= 0x80

  if (val >= mul) val -= Math.pow(2, 8 * byteLength)

  return val
}

Buffer.prototype.readIntBE = function readIntBE (offset, byteLength, noAssert) {
  offset = offset | 0
  byteLength = byteLength | 0
  if (!noAssert) checkOffset(offset, byteLength, this.length)

  var i = byteLength
  var mul = 1
  var val = this[offset + --i]
  while (i > 0 && (mul *= 0x100)) {
    val += this[offset + --i] * mul
  }
  mul *= 0x80

  if (val >= mul) val -= Math.pow(2, 8 * byteLength)

  return val
}

Buffer.prototype.readInt8 = function readInt8 (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 1, this.length)
  if (!(this[offset] & 0x80)) return (this[offset])
  return ((0xff - this[offset] + 1) * -1)
}

Buffer.prototype.readInt16LE = function readInt16LE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 2, this.length)
  var val = this[offset] | (this[offset + 1] << 8)
  return (val & 0x8000) ? val | 0xFFFF0000 : val
}

Buffer.prototype.readInt16BE = function readInt16BE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 2, this.length)
  var val = this[offset + 1] | (this[offset] << 8)
  return (val & 0x8000) ? val | 0xFFFF0000 : val
}

Buffer.prototype.readInt32LE = function readInt32LE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 4, this.length)

  return (this[offset]) |
    (this[offset + 1] << 8) |
    (this[offset + 2] << 16) |
    (this[offset + 3] << 24)
}

Buffer.prototype.readInt32BE = function readInt32BE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 4, this.length)

  return (this[offset] << 24) |
    (this[offset + 1] << 16) |
    (this[offset + 2] << 8) |
    (this[offset + 3])
}

Buffer.prototype.readFloatLE = function readFloatLE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 4, this.length)
  return ieee754.read(this, offset, true, 23, 4)
}

Buffer.prototype.readFloatBE = function readFloatBE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 4, this.length)
  return ieee754.read(this, offset, false, 23, 4)
}

Buffer.prototype.readDoubleLE = function readDoubleLE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 8, this.length)
  return ieee754.read(this, offset, true, 52, 8)
}

Buffer.prototype.readDoubleBE = function readDoubleBE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 8, this.length)
  return ieee754.read(this, offset, false, 52, 8)
}

function checkInt (buf, value, offset, ext, max, min) {
  if (!Buffer.isBuffer(buf)) throw new TypeError('"buffer" argument must be a Buffer instance')
  if (value > max || value < min) throw new RangeError('"value" argument is out of bounds')
  if (offset + ext > buf.length) throw new RangeError('Index out of range')
}

Buffer.prototype.writeUIntLE = function writeUIntLE (value, offset, byteLength, noAssert) {
  value = +value
  offset = offset | 0
  byteLength = byteLength | 0
  if (!noAssert) {
    var maxBytes = Math.pow(2, 8 * byteLength) - 1
    checkInt(this, value, offset, byteLength, maxBytes, 0)
  }

  var mul = 1
  var i = 0
  this[offset] = value & 0xFF
  while (++i < byteLength && (mul *= 0x100)) {
    this[offset + i] = (value / mul) & 0xFF
  }

  return offset + byteLength
}

Buffer.prototype.writeUIntBE = function writeUIntBE (value, offset, byteLength, noAssert) {
  value = +value
  offset = offset | 0
  byteLength = byteLength | 0
  if (!noAssert) {
    var maxBytes = Math.pow(2, 8 * byteLength) - 1
    checkInt(this, value, offset, byteLength, maxBytes, 0)
  }

  var i = byteLength - 1
  var mul = 1
  this[offset + i] = value & 0xFF
  while (--i >= 0 && (mul *= 0x100)) {
    this[offset + i] = (value / mul) & 0xFF
  }

  return offset + byteLength
}

Buffer.prototype.writeUInt8 = function writeUInt8 (value, offset, noAssert) {
  value = +value
  offset = offset | 0
  if (!noAssert) checkInt(this, value, offset, 1, 0xff, 0)
  if (!Buffer.TYPED_ARRAY_SUPPORT) value = Math.floor(value)
  this[offset] = (value & 0xff)
  return offset + 1
}

function objectWriteUInt16 (buf, value, offset, littleEndian) {
  if (value < 0) value = 0xffff + value + 1
  for (var i = 0, j = Math.min(buf.length - offset, 2); i < j; ++i) {
    buf[offset + i] = (value & (0xff << (8 * (littleEndian ? i : 1 - i)))) >>>
      (littleEndian ? i : 1 - i) * 8
  }
}

Buffer.prototype.writeUInt16LE = function writeUInt16LE (value, offset, noAssert) {
  value = +value
  offset = offset | 0
  if (!noAssert) checkInt(this, value, offset, 2, 0xffff, 0)
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    this[offset] = (value & 0xff)
    this[offset + 1] = (value >>> 8)
  } else {
    objectWriteUInt16(this, value, offset, true)
  }
  return offset + 2
}

Buffer.prototype.writeUInt16BE = function writeUInt16BE (value, offset, noAssert) {
  value = +value
  offset = offset | 0
  if (!noAssert) checkInt(this, value, offset, 2, 0xffff, 0)
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    this[offset] = (value >>> 8)
    this[offset + 1] = (value & 0xff)
  } else {
    objectWriteUInt16(this, value, offset, false)
  }
  return offset + 2
}

function objectWriteUInt32 (buf, value, offset, littleEndian) {
  if (value < 0) value = 0xffffffff + value + 1
  for (var i = 0, j = Math.min(buf.length - offset, 4); i < j; ++i) {
    buf[offset + i] = (value >>> (littleEndian ? i : 3 - i) * 8) & 0xff
  }
}

Buffer.prototype.writeUInt32LE = function writeUInt32LE (value, offset, noAssert) {
  value = +value
  offset = offset | 0
  if (!noAssert) checkInt(this, value, offset, 4, 0xffffffff, 0)
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    this[offset + 3] = (value >>> 24)
    this[offset + 2] = (value >>> 16)
    this[offset + 1] = (value >>> 8)
    this[offset] = (value & 0xff)
  } else {
    objectWriteUInt32(this, value, offset, true)
  }
  return offset + 4
}

Buffer.prototype.writeUInt32BE = function writeUInt32BE (value, offset, noAssert) {
  value = +value
  offset = offset | 0
  if (!noAssert) checkInt(this, value, offset, 4, 0xffffffff, 0)
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    this[offset] = (value >>> 24)
    this[offset + 1] = (value >>> 16)
    this[offset + 2] = (value >>> 8)
    this[offset + 3] = (value & 0xff)
  } else {
    objectWriteUInt32(this, value, offset, false)
  }
  return offset + 4
}

Buffer.prototype.writeIntLE = function writeIntLE (value, offset, byteLength, noAssert) {
  value = +value
  offset = offset | 0
  if (!noAssert) {
    var limit = Math.pow(2, 8 * byteLength - 1)

    checkInt(this, value, offset, byteLength, limit - 1, -limit)
  }

  var i = 0
  var mul = 1
  var sub = 0
  this[offset] = value & 0xFF
  while (++i < byteLength && (mul *= 0x100)) {
    if (value < 0 && sub === 0 && this[offset + i - 1] !== 0) {
      sub = 1
    }
    this[offset + i] = ((value / mul) >> 0) - sub & 0xFF
  }

  return offset + byteLength
}

Buffer.prototype.writeIntBE = function writeIntBE (value, offset, byteLength, noAssert) {
  value = +value
  offset = offset | 0
  if (!noAssert) {
    var limit = Math.pow(2, 8 * byteLength - 1)

    checkInt(this, value, offset, byteLength, limit - 1, -limit)
  }

  var i = byteLength - 1
  var mul = 1
  var sub = 0
  this[offset + i] = value & 0xFF
  while (--i >= 0 && (mul *= 0x100)) {
    if (value < 0 && sub === 0 && this[offset + i + 1] !== 0) {
      sub = 1
    }
    this[offset + i] = ((value / mul) >> 0) - sub & 0xFF
  }

  return offset + byteLength
}

Buffer.prototype.writeInt8 = function writeInt8 (value, offset, noAssert) {
  value = +value
  offset = offset | 0
  if (!noAssert) checkInt(this, value, offset, 1, 0x7f, -0x80)
  if (!Buffer.TYPED_ARRAY_SUPPORT) value = Math.floor(value)
  if (value < 0) value = 0xff + value + 1
  this[offset] = (value & 0xff)
  return offset + 1
}

Buffer.prototype.writeInt16LE = function writeInt16LE (value, offset, noAssert) {
  value = +value
  offset = offset | 0
  if (!noAssert) checkInt(this, value, offset, 2, 0x7fff, -0x8000)
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    this[offset] = (value & 0xff)
    this[offset + 1] = (value >>> 8)
  } else {
    objectWriteUInt16(this, value, offset, true)
  }
  return offset + 2
}

Buffer.prototype.writeInt16BE = function writeInt16BE (value, offset, noAssert) {
  value = +value
  offset = offset | 0
  if (!noAssert) checkInt(this, value, offset, 2, 0x7fff, -0x8000)
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    this[offset] = (value >>> 8)
    this[offset + 1] = (value & 0xff)
  } else {
    objectWriteUInt16(this, value, offset, false)
  }
  return offset + 2
}

Buffer.prototype.writeInt32LE = function writeInt32LE (value, offset, noAssert) {
  value = +value
  offset = offset | 0
  if (!noAssert) checkInt(this, value, offset, 4, 0x7fffffff, -0x80000000)
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    this[offset] = (value & 0xff)
    this[offset + 1] = (value >>> 8)
    this[offset + 2] = (value >>> 16)
    this[offset + 3] = (value >>> 24)
  } else {
    objectWriteUInt32(this, value, offset, true)
  }
  return offset + 4
}

Buffer.prototype.writeInt32BE = function writeInt32BE (value, offset, noAssert) {
  value = +value
  offset = offset | 0
  if (!noAssert) checkInt(this, value, offset, 4, 0x7fffffff, -0x80000000)
  if (value < 0) value = 0xffffffff + value + 1
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    this[offset] = (value >>> 24)
    this[offset + 1] = (value >>> 16)
    this[offset + 2] = (value >>> 8)
    this[offset + 3] = (value & 0xff)
  } else {
    objectWriteUInt32(this, value, offset, false)
  }
  return offset + 4
}

function checkIEEE754 (buf, value, offset, ext, max, min) {
  if (offset + ext > buf.length) throw new RangeError('Index out of range')
  if (offset < 0) throw new RangeError('Index out of range')
}

function writeFloat (buf, value, offset, littleEndian, noAssert) {
  if (!noAssert) {
    checkIEEE754(buf, value, offset, 4, 3.4028234663852886e+38, -3.4028234663852886e+38)
  }
  ieee754.write(buf, value, offset, littleEndian, 23, 4)
  return offset + 4
}

Buffer.prototype.writeFloatLE = function writeFloatLE (value, offset, noAssert) {
  return writeFloat(this, value, offset, true, noAssert)
}

Buffer.prototype.writeFloatBE = function writeFloatBE (value, offset, noAssert) {
  return writeFloat(this, value, offset, false, noAssert)
}

function writeDouble (buf, value, offset, littleEndian, noAssert) {
  if (!noAssert) {
    checkIEEE754(buf, value, offset, 8, 1.7976931348623157E+308, -1.7976931348623157E+308)
  }
  ieee754.write(buf, value, offset, littleEndian, 52, 8)
  return offset + 8
}

Buffer.prototype.writeDoubleLE = function writeDoubleLE (value, offset, noAssert) {
  return writeDouble(this, value, offset, true, noAssert)
}

Buffer.prototype.writeDoubleBE = function writeDoubleBE (value, offset, noAssert) {
  return writeDouble(this, value, offset, false, noAssert)
}

// copy(targetBuffer, targetStart=0, sourceStart=0, sourceEnd=buffer.length)
Buffer.prototype.copy = function copy (target, targetStart, start, end) {
  if (!start) start = 0
  if (!end && end !== 0) end = this.length
  if (targetStart >= target.length) targetStart = target.length
  if (!targetStart) targetStart = 0
  if (end > 0 && end < start) end = start

  // Copy 0 bytes; we're done
  if (end === start) return 0
  if (target.length === 0 || this.length === 0) return 0

  // Fatal error conditions
  if (targetStart < 0) {
    throw new RangeError('targetStart out of bounds')
  }
  if (start < 0 || start >= this.length) throw new RangeError('sourceStart out of bounds')
  if (end < 0) throw new RangeError('sourceEnd out of bounds')

  // Are we oob?
  if (end > this.length) end = this.length
  if (target.length - targetStart < end - start) {
    end = target.length - targetStart + start
  }

  var len = end - start
  var i

  if (this === target && start < targetStart && targetStart < end) {
    // descending copy from end
    for (i = len - 1; i >= 0; --i) {
      target[i + targetStart] = this[i + start]
    }
  } else if (len < 1000 || !Buffer.TYPED_ARRAY_SUPPORT) {
    // ascending copy from start
    for (i = 0; i < len; ++i) {
      target[i + targetStart] = this[i + start]
    }
  } else {
    Uint8Array.prototype.set.call(
      target,
      this.subarray(start, start + len),
      targetStart
    )
  }

  return len
}

// Usage:
//    buffer.fill(number[, offset[, end]])
//    buffer.fill(buffer[, offset[, end]])
//    buffer.fill(string[, offset[, end]][, encoding])
Buffer.prototype.fill = function fill (val, start, end, encoding) {
  // Handle string cases:
  if (typeof val === 'string') {
    if (typeof start === 'string') {
      encoding = start
      start = 0
      end = this.length
    } else if (typeof end === 'string') {
      encoding = end
      end = this.length
    }
    if (val.length === 1) {
      var code = val.charCodeAt(0)
      if (code < 256) {
        val = code
      }
    }
    if (encoding !== undefined && typeof encoding !== 'string') {
      throw new TypeError('encoding must be a string')
    }
    if (typeof encoding === 'string' && !Buffer.isEncoding(encoding)) {
      throw new TypeError('Unknown encoding: ' + encoding)
    }
  } else if (typeof val === 'number') {
    val = val & 255
  }

  // Invalid ranges are not set to a default, so can range check early.
  if (start < 0 || this.length < start || this.length < end) {
    throw new RangeError('Out of range index')
  }

  if (end <= start) {
    return this
  }

  start = start >>> 0
  end = end === undefined ? this.length : end >>> 0

  if (!val) val = 0

  var i
  if (typeof val === 'number') {
    for (i = start; i < end; ++i) {
      this[i] = val
    }
  } else {
    var bytes = Buffer.isBuffer(val)
      ? val
      : utf8ToBytes(new Buffer(val, encoding).toString())
    var len = bytes.length
    for (i = 0; i < end - start; ++i) {
      this[i + start] = bytes[i % len]
    }
  }

  return this
}

// HELPER FUNCTIONS
// ================

var INVALID_BASE64_RE = /[^+\/0-9A-Za-z-_]/g

function base64clean (str) {
  // Node strips out invalid characters like \n and \t from the string, base64-js does not
  str = stringtrim(str).replace(INVALID_BASE64_RE, '')
  // Node converts strings with length < 2 to ''
  if (str.length < 2) return ''
  // Node allows for non-padded base64 strings (missing trailing ===), base64-js does not
  while (str.length % 4 !== 0) {
    str = str + '='
  }
  return str
}

function stringtrim (str) {
  if (str.trim) return str.trim()
  return str.replace(/^\s+|\s+$/g, '')
}

function toHex (n) {
  if (n < 16) return '0' + n.toString(16)
  return n.toString(16)
}

function utf8ToBytes (string, units) {
  units = units || Infinity
  var codePoint
  var length = string.length
  var leadSurrogate = null
  var bytes = []

  for (var i = 0; i < length; ++i) {
    codePoint = string.charCodeAt(i)

    // is surrogate component
    if (codePoint > 0xD7FF && codePoint < 0xE000) {
      // last char was a lead
      if (!leadSurrogate) {
        // no lead yet
        if (codePoint > 0xDBFF) {
          // unexpected trail
          if ((units -= 3) > -1) bytes.push(0xEF, 0xBF, 0xBD)
          continue
        } else if (i + 1 === length) {
          // unpaired lead
          if ((units -= 3) > -1) bytes.push(0xEF, 0xBF, 0xBD)
          continue
        }

        // valid lead
        leadSurrogate = codePoint

        continue
      }

      // 2 leads in a row
      if (codePoint < 0xDC00) {
        if ((units -= 3) > -1) bytes.push(0xEF, 0xBF, 0xBD)
        leadSurrogate = codePoint
        continue
      }

      // valid surrogate pair
      codePoint = (leadSurrogate - 0xD800 << 10 | codePoint - 0xDC00) + 0x10000
    } else if (leadSurrogate) {
      // valid bmp char, but last char was a lead
      if ((units -= 3) > -1) bytes.push(0xEF, 0xBF, 0xBD)
    }

    leadSurrogate = null

    // encode utf8
    if (codePoint < 0x80) {
      if ((units -= 1) < 0) break
      bytes.push(codePoint)
    } else if (codePoint < 0x800) {
      if ((units -= 2) < 0) break
      bytes.push(
        codePoint >> 0x6 | 0xC0,
        codePoint & 0x3F | 0x80
      )
    } else if (codePoint < 0x10000) {
      if ((units -= 3) < 0) break
      bytes.push(
        codePoint >> 0xC | 0xE0,
        codePoint >> 0x6 & 0x3F | 0x80,
        codePoint & 0x3F | 0x80
      )
    } else if (codePoint < 0x110000) {
      if ((units -= 4) < 0) break
      bytes.push(
        codePoint >> 0x12 | 0xF0,
        codePoint >> 0xC & 0x3F | 0x80,
        codePoint >> 0x6 & 0x3F | 0x80,
        codePoint & 0x3F | 0x80
      )
    } else {
      throw new Error('Invalid code point')
    }
  }

  return bytes
}

function asciiToBytes (str) {
  var byteArray = []
  for (var i = 0; i < str.length; ++i) {
    // Node's code seems to be doing this and not & 0x7F..
    byteArray.push(str.charCodeAt(i) & 0xFF)
  }
  return byteArray
}

function utf16leToBytes (str, units) {
  var c, hi, lo
  var byteArray = []
  for (var i = 0; i < str.length; ++i) {
    if ((units -= 2) < 0) break

    c = str.charCodeAt(i)
    hi = c >> 8
    lo = c % 256
    byteArray.push(lo)
    byteArray.push(hi)
  }

  return byteArray
}

function base64ToBytes (str) {
  return base64.toByteArray(base64clean(str))
}

function blitBuffer (src, dst, offset, length) {
  for (var i = 0; i < length; ++i) {
    if ((i + offset >= dst.length) || (i >= src.length)) break
    dst[i + offset] = src[i]
  }
  return i
}

function isnan (val) {
  return val !== val // eslint-disable-line no-self-compare
}

/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./../webpack/buildin/global.js */ "./node_modules/webpack/buildin/global.js")))

/***/ }),

/***/ "./node_modules/buffer/node_modules/isarray/index.js":
/*!***********************************************************!*\
  !*** ./node_modules/buffer/node_modules/isarray/index.js ***!
  \***********************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

var toString = {}.toString;

module.exports = Array.isArray || function (arr) {
  return toString.call(arr) == '[object Array]';
};


/***/ }),

/***/ "./node_modules/component-bind/index.js":
/*!**********************************************!*\
  !*** ./node_modules/component-bind/index.js ***!
  \**********************************************/
/*! no static exports found */
/***/ (function(module, exports) {

/**
 * Slice reference.
 */

var slice = [].slice;

/**
 * Bind `obj` to `fn`.
 *
 * @param {Object} obj
 * @param {Function|String} fn or string
 * @return {Function}
 * @api public
 */

module.exports = function(obj, fn){
  if ('string' == typeof fn) fn = obj[fn];
  if ('function' != typeof fn) throw new Error('bind() requires a function');
  var args = slice.call(arguments, 2);
  return function(){
    return fn.apply(obj, args.concat(slice.call(arguments)));
  }
};


/***/ }),

/***/ "./node_modules/component-emitter/index.js":
/*!*************************************************!*\
  !*** ./node_modules/component-emitter/index.js ***!
  \*************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {


/**
 * Expose `Emitter`.
 */

if (true) {
  module.exports = Emitter;
}

/**
 * Initialize a new `Emitter`.
 *
 * @api public
 */

function Emitter(obj) {
  if (obj) return mixin(obj);
};

/**
 * Mixin the emitter properties.
 *
 * @param {Object} obj
 * @return {Object}
 * @api private
 */

function mixin(obj) {
  for (var key in Emitter.prototype) {
    obj[key] = Emitter.prototype[key];
  }
  return obj;
}

/**
 * Listen on the given `event` with `fn`.
 *
 * @param {String} event
 * @param {Function} fn
 * @return {Emitter}
 * @api public
 */

Emitter.prototype.on =
Emitter.prototype.addEventListener = function(event, fn){
  this._callbacks = this._callbacks || {};
  (this._callbacks['$' + event] = this._callbacks['$' + event] || [])
    .push(fn);
  return this;
};

/**
 * Adds an `event` listener that will be invoked a single
 * time then automatically removed.
 *
 * @param {String} event
 * @param {Function} fn
 * @return {Emitter}
 * @api public
 */

Emitter.prototype.once = function(event, fn){
  function on() {
    this.off(event, on);
    fn.apply(this, arguments);
  }

  on.fn = fn;
  this.on(event, on);
  return this;
};

/**
 * Remove the given callback for `event` or all
 * registered callbacks.
 *
 * @param {String} event
 * @param {Function} fn
 * @return {Emitter}
 * @api public
 */

Emitter.prototype.off =
Emitter.prototype.removeListener =
Emitter.prototype.removeAllListeners =
Emitter.prototype.removeEventListener = function(event, fn){
  this._callbacks = this._callbacks || {};

  // all
  if (0 == arguments.length) {
    this._callbacks = {};
    return this;
  }

  // specific event
  var callbacks = this._callbacks['$' + event];
  if (!callbacks) return this;

  // remove all handlers
  if (1 == arguments.length) {
    delete this._callbacks['$' + event];
    return this;
  }

  // remove specific handler
  var cb;
  for (var i = 0; i < callbacks.length; i++) {
    cb = callbacks[i];
    if (cb === fn || cb.fn === fn) {
      callbacks.splice(i, 1);
      break;
    }
  }
  return this;
};

/**
 * Emit `event` with the given args.
 *
 * @param {String} event
 * @param {Mixed} ...
 * @return {Emitter}
 */

Emitter.prototype.emit = function(event){
  this._callbacks = this._callbacks || {};
  var args = [].slice.call(arguments, 1)
    , callbacks = this._callbacks['$' + event];

  if (callbacks) {
    callbacks = callbacks.slice(0);
    for (var i = 0, len = callbacks.length; i < len; ++i) {
      callbacks[i].apply(this, args);
    }
  }

  return this;
};

/**
 * Return array of callbacks for `event`.
 *
 * @param {String} event
 * @return {Array}
 * @api public
 */

Emitter.prototype.listeners = function(event){
  this._callbacks = this._callbacks || {};
  return this._callbacks['$' + event] || [];
};

/**
 * Check if this emitter has `event` handlers.
 *
 * @param {String} event
 * @return {Boolean}
 * @api public
 */

Emitter.prototype.hasListeners = function(event){
  return !! this.listeners(event).length;
};


/***/ }),

/***/ "./node_modules/component-inherit/index.js":
/*!*************************************************!*\
  !*** ./node_modules/component-inherit/index.js ***!
  \*************************************************/
/*! no static exports found */
/***/ (function(module, exports) {


module.exports = function(a, b){
  var fn = function(){};
  fn.prototype = b.prototype;
  a.prototype = new fn;
  a.prototype.constructor = a;
};

/***/ }),

/***/ "./node_modules/debug/src/browser.js":
/*!*******************************************!*\
  !*** ./node_modules/debug/src/browser.js ***!
  \*******************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

/* WEBPACK VAR INJECTION */(function(process) {/* eslint-env browser */

/**
 * This is the web browser implementation of `debug()`.
 */

exports.log = log;
exports.formatArgs = formatArgs;
exports.save = save;
exports.load = load;
exports.useColors = useColors;
exports.storage = localstorage();

/**
 * Colors.
 */

exports.colors = [
	'#0000CC',
	'#0000FF',
	'#0033CC',
	'#0033FF',
	'#0066CC',
	'#0066FF',
	'#0099CC',
	'#0099FF',
	'#00CC00',
	'#00CC33',
	'#00CC66',
	'#00CC99',
	'#00CCCC',
	'#00CCFF',
	'#3300CC',
	'#3300FF',
	'#3333CC',
	'#3333FF',
	'#3366CC',
	'#3366FF',
	'#3399CC',
	'#3399FF',
	'#33CC00',
	'#33CC33',
	'#33CC66',
	'#33CC99',
	'#33CCCC',
	'#33CCFF',
	'#6600CC',
	'#6600FF',
	'#6633CC',
	'#6633FF',
	'#66CC00',
	'#66CC33',
	'#9900CC',
	'#9900FF',
	'#9933CC',
	'#9933FF',
	'#99CC00',
	'#99CC33',
	'#CC0000',
	'#CC0033',
	'#CC0066',
	'#CC0099',
	'#CC00CC',
	'#CC00FF',
	'#CC3300',
	'#CC3333',
	'#CC3366',
	'#CC3399',
	'#CC33CC',
	'#CC33FF',
	'#CC6600',
	'#CC6633',
	'#CC9900',
	'#CC9933',
	'#CCCC00',
	'#CCCC33',
	'#FF0000',
	'#FF0033',
	'#FF0066',
	'#FF0099',
	'#FF00CC',
	'#FF00FF',
	'#FF3300',
	'#FF3333',
	'#FF3366',
	'#FF3399',
	'#FF33CC',
	'#FF33FF',
	'#FF6600',
	'#FF6633',
	'#FF9900',
	'#FF9933',
	'#FFCC00',
	'#FFCC33'
];

/**
 * Currently only WebKit-based Web Inspectors, Firefox >= v31,
 * and the Firebug extension (any Firefox version) are known
 * to support "%c" CSS customizations.
 *
 * TODO: add a `localStorage` variable to explicitly enable/disable colors
 */

// eslint-disable-next-line complexity
function useColors() {
	// NB: In an Electron preload script, document will be defined but not fully
	// initialized. Since we know we're in Chrome, we'll just detect this case
	// explicitly
	if (typeof window !== 'undefined' && window.process && (window.process.type === 'renderer' || window.process.__nwjs)) {
		return true;
	}

	// Internet Explorer and Edge do not support colors.
	if (typeof navigator !== 'undefined' && navigator.userAgent && navigator.userAgent.toLowerCase().match(/(edge|trident)\/(\d+)/)) {
		return false;
	}

	// Is webkit? http://stackoverflow.com/a/16459606/376773
	// document is undefined in react-native: https://github.com/facebook/react-native/pull/1632
	return (typeof document !== 'undefined' && document.documentElement && document.documentElement.style && document.documentElement.style.WebkitAppearance) ||
		// Is firebug? http://stackoverflow.com/a/398120/376773
		(typeof window !== 'undefined' && window.console && (window.console.firebug || (window.console.exception && window.console.table))) ||
		// Is firefox >= v31?
		// https://developer.mozilla.org/en-US/docs/Tools/Web_Console#Styling_messages
		(typeof navigator !== 'undefined' && navigator.userAgent && navigator.userAgent.toLowerCase().match(/firefox\/(\d+)/) && parseInt(RegExp.$1, 10) >= 31) ||
		// Double check webkit in userAgent just in case we are in a worker
		(typeof navigator !== 'undefined' && navigator.userAgent && navigator.userAgent.toLowerCase().match(/applewebkit\/(\d+)/));
}

/**
 * Colorize log arguments if enabled.
 *
 * @api public
 */

function formatArgs(args) {
	args[0] = (this.useColors ? '%c' : '') +
		this.namespace +
		(this.useColors ? ' %c' : ' ') +
		args[0] +
		(this.useColors ? '%c ' : ' ') +
		'+' + module.exports.humanize(this.diff);

	if (!this.useColors) {
		return;
	}

	const c = 'color: ' + this.color;
	args.splice(1, 0, c, 'color: inherit');

	// The final "%c" is somewhat tricky, because there could be other
	// arguments passed either before or after the %c, so we need to
	// figure out the correct index to insert the CSS into
	let index = 0;
	let lastC = 0;
	args[0].replace(/%[a-zA-Z%]/g, match => {
		if (match === '%%') {
			return;
		}
		index++;
		if (match === '%c') {
			// We only are interested in the *last* %c
			// (the user may have provided their own)
			lastC = index;
		}
	});

	args.splice(lastC, 0, c);
}

/**
 * Invokes `console.log()` when available.
 * No-op when `console.log` is not a "function".
 *
 * @api public
 */
function log(...args) {
	// This hackery is required for IE8/9, where
	// the `console.log` function doesn't have 'apply'
	return typeof console === 'object' &&
		console.log &&
		console.log(...args);
}

/**
 * Save `namespaces`.
 *
 * @param {String} namespaces
 * @api private
 */
function save(namespaces) {
	try {
		if (namespaces) {
			exports.storage.setItem('debug', namespaces);
		} else {
			exports.storage.removeItem('debug');
		}
	} catch (error) {
		// Swallow
		// XXX (@Qix-) should we be logging these?
	}
}

/**
 * Load `namespaces`.
 *
 * @return {String} returns the previously persisted debug modes
 * @api private
 */
function load() {
	let r;
	try {
		r = exports.storage.getItem('debug');
	} catch (error) {
		// Swallow
		// XXX (@Qix-) should we be logging these?
	}

	// If debug isn't set in LS, and we're in Electron, try to load $DEBUG
	if (!r && typeof process !== 'undefined' && 'env' in process) {
		r = process.env.DEBUG;
	}

	return r;
}

/**
 * Localstorage attempts to return the localstorage.
 *
 * This is necessary because safari throws
 * when a user disables cookies/localstorage
 * and you attempt to access it.
 *
 * @return {LocalStorage}
 * @api private
 */

function localstorage() {
	try {
		// TVMLKit (Apple TV JS Runtime) does not have a window object, just localStorage in the global context
		// The Browser also has localStorage in the global context.
		return localStorage;
	} catch (error) {
		// Swallow
		// XXX (@Qix-) should we be logging these?
	}
}

module.exports = __webpack_require__(/*! ./common */ "./node_modules/debug/src/common.js")(exports);

const {formatters} = module.exports;

/**
 * Map %j to `JSON.stringify()`, since no Web Inspectors do that by default.
 */

formatters.j = function (v) {
	try {
		return JSON.stringify(v);
	} catch (error) {
		return '[UnexpectedJSONParseError]: ' + error.message;
	}
};

/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./../../process/browser.js */ "./node_modules/process/browser.js")))

/***/ }),

/***/ "./node_modules/debug/src/common.js":
/*!******************************************!*\
  !*** ./node_modules/debug/src/common.js ***!
  \******************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {


/**
 * This is the common logic for both the Node.js and web browser
 * implementations of `debug()`.
 */

function setup(env) {
	createDebug.debug = createDebug;
	createDebug.default = createDebug;
	createDebug.coerce = coerce;
	createDebug.disable = disable;
	createDebug.enable = enable;
	createDebug.enabled = enabled;
	createDebug.humanize = __webpack_require__(/*! ms */ "./node_modules/ms/index.js");

	Object.keys(env).forEach(key => {
		createDebug[key] = env[key];
	});

	/**
	* Active `debug` instances.
	*/
	createDebug.instances = [];

	/**
	* The currently active debug mode names, and names to skip.
	*/

	createDebug.names = [];
	createDebug.skips = [];

	/**
	* Map of special "%n" handling functions, for the debug "format" argument.
	*
	* Valid key names are a single, lower or upper-case letter, i.e. "n" and "N".
	*/
	createDebug.formatters = {};

	/**
	* Selects a color for a debug namespace
	* @param {String} namespace The namespace string for the for the debug instance to be colored
	* @return {Number|String} An ANSI color code for the given namespace
	* @api private
	*/
	function selectColor(namespace) {
		let hash = 0;

		for (let i = 0; i < namespace.length; i++) {
			hash = ((hash << 5) - hash) + namespace.charCodeAt(i);
			hash |= 0; // Convert to 32bit integer
		}

		return createDebug.colors[Math.abs(hash) % createDebug.colors.length];
	}
	createDebug.selectColor = selectColor;

	/**
	* Create a debugger with the given `namespace`.
	*
	* @param {String} namespace
	* @return {Function}
	* @api public
	*/
	function createDebug(namespace) {
		let prevTime;

		function debug(...args) {
			// Disabled?
			if (!debug.enabled) {
				return;
			}

			const self = debug;

			// Set `diff` timestamp
			const curr = Number(new Date());
			const ms = curr - (prevTime || curr);
			self.diff = ms;
			self.prev = prevTime;
			self.curr = curr;
			prevTime = curr;

			args[0] = createDebug.coerce(args[0]);

			if (typeof args[0] !== 'string') {
				// Anything else let's inspect with %O
				args.unshift('%O');
			}

			// Apply any `formatters` transformations
			let index = 0;
			args[0] = args[0].replace(/%([a-zA-Z%])/g, (match, format) => {
				// If we encounter an escaped % then don't increase the array index
				if (match === '%%') {
					return match;
				}
				index++;
				const formatter = createDebug.formatters[format];
				if (typeof formatter === 'function') {
					const val = args[index];
					match = formatter.call(self, val);

					// Now we need to remove `args[index]` since it's inlined in the `format`
					args.splice(index, 1);
					index--;
				}
				return match;
			});

			// Apply env-specific formatting (colors, etc.)
			createDebug.formatArgs.call(self, args);

			const logFn = self.log || createDebug.log;
			logFn.apply(self, args);
		}

		debug.namespace = namespace;
		debug.enabled = createDebug.enabled(namespace);
		debug.useColors = createDebug.useColors();
		debug.color = selectColor(namespace);
		debug.destroy = destroy;
		debug.extend = extend;
		// Debug.formatArgs = formatArgs;
		// debug.rawLog = rawLog;

		// env-specific initialization logic for debug instances
		if (typeof createDebug.init === 'function') {
			createDebug.init(debug);
		}

		createDebug.instances.push(debug);

		return debug;
	}

	function destroy() {
		const index = createDebug.instances.indexOf(this);
		if (index !== -1) {
			createDebug.instances.splice(index, 1);
			return true;
		}
		return false;
	}

	function extend(namespace, delimiter) {
		const newDebug = createDebug(this.namespace + (typeof delimiter === 'undefined' ? ':' : delimiter) + namespace);
		newDebug.log = this.log;
		return newDebug;
	}

	/**
	* Enables a debug mode by namespaces. This can include modes
	* separated by a colon and wildcards.
	*
	* @param {String} namespaces
	* @api public
	*/
	function enable(namespaces) {
		createDebug.save(namespaces);

		createDebug.names = [];
		createDebug.skips = [];

		let i;
		const split = (typeof namespaces === 'string' ? namespaces : '').split(/[\s,]+/);
		const len = split.length;

		for (i = 0; i < len; i++) {
			if (!split[i]) {
				// ignore empty strings
				continue;
			}

			namespaces = split[i].replace(/\*/g, '.*?');

			if (namespaces[0] === '-') {
				createDebug.skips.push(new RegExp('^' + namespaces.substr(1) + '$'));
			} else {
				createDebug.names.push(new RegExp('^' + namespaces + '$'));
			}
		}

		for (i = 0; i < createDebug.instances.length; i++) {
			const instance = createDebug.instances[i];
			instance.enabled = createDebug.enabled(instance.namespace);
		}
	}

	/**
	* Disable debug output.
	*
	* @return {String} namespaces
	* @api public
	*/
	function disable() {
		const namespaces = [
			...createDebug.names.map(toNamespace),
			...createDebug.skips.map(toNamespace).map(namespace => '-' + namespace)
		].join(',');
		createDebug.enable('');
		return namespaces;
	}

	/**
	* Returns true if the given mode name is enabled, false otherwise.
	*
	* @param {String} name
	* @return {Boolean}
	* @api public
	*/
	function enabled(name) {
		if (name[name.length - 1] === '*') {
			return true;
		}

		let i;
		let len;

		for (i = 0, len = createDebug.skips.length; i < len; i++) {
			if (createDebug.skips[i].test(name)) {
				return false;
			}
		}

		for (i = 0, len = createDebug.names.length; i < len; i++) {
			if (createDebug.names[i].test(name)) {
				return true;
			}
		}

		return false;
	}

	/**
	* Convert regexp to namespace
	*
	* @param {RegExp} regxep
	* @return {String} namespace
	* @api private
	*/
	function toNamespace(regexp) {
		return regexp.toString()
			.substring(2, regexp.toString().length - 2)
			.replace(/\.\*\?$/, '*');
	}

	/**
	* Coerce `val`.
	*
	* @param {Mixed} val
	* @return {Mixed}
	* @api private
	*/
	function coerce(val) {
		if (val instanceof Error) {
			return val.stack || val.message;
		}
		return val;
	}

	createDebug.enable(createDebug.load());

	return createDebug;
}

module.exports = setup;


/***/ }),

/***/ "./node_modules/engine.io-client/lib/index.js":
/*!****************************************************!*\
  !*** ./node_modules/engine.io-client/lib/index.js ***!
  \****************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {


module.exports = __webpack_require__(/*! ./socket */ "./node_modules/engine.io-client/lib/socket.js");

/**
 * Exports parser
 *
 * @api public
 *
 */
module.exports.parser = __webpack_require__(/*! engine.io-parser */ "./node_modules/engine.io-parser/lib/browser.js");


/***/ }),

/***/ "./node_modules/engine.io-client/lib/socket.js":
/*!*****************************************************!*\
  !*** ./node_modules/engine.io-client/lib/socket.js ***!
  \*****************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

/**
 * Module dependencies.
 */

var transports = __webpack_require__(/*! ./transports/index */ "./node_modules/engine.io-client/lib/transports/index.js");
var Emitter = __webpack_require__(/*! component-emitter */ "./node_modules/component-emitter/index.js");
var debug = __webpack_require__(/*! debug */ "./node_modules/debug/src/browser.js")('engine.io-client:socket');
var index = __webpack_require__(/*! indexof */ "./node_modules/indexof/index.js");
var parser = __webpack_require__(/*! engine.io-parser */ "./node_modules/engine.io-parser/lib/browser.js");
var parseuri = __webpack_require__(/*! parseuri */ "./node_modules/parseuri/index.js");
var parseqs = __webpack_require__(/*! parseqs */ "./node_modules/parseqs/index.js");

/**
 * Module exports.
 */

module.exports = Socket;

/**
 * Socket constructor.
 *
 * @param {String|Object} uri or options
 * @param {Object} options
 * @api public
 */

function Socket (uri, opts) {
  if (!(this instanceof Socket)) return new Socket(uri, opts);

  opts = opts || {};

  if (uri && 'object' === typeof uri) {
    opts = uri;
    uri = null;
  }

  if (uri) {
    uri = parseuri(uri);
    opts.hostname = uri.host;
    opts.secure = uri.protocol === 'https' || uri.protocol === 'wss';
    opts.port = uri.port;
    if (uri.query) opts.query = uri.query;
  } else if (opts.host) {
    opts.hostname = parseuri(opts.host).host;
  }

  this.secure = null != opts.secure ? opts.secure
    : (typeof location !== 'undefined' && 'https:' === location.protocol);

  if (opts.hostname && !opts.port) {
    // if no port is specified manually, use the protocol default
    opts.port = this.secure ? '443' : '80';
  }

  this.agent = opts.agent || false;
  this.hostname = opts.hostname ||
    (typeof location !== 'undefined' ? location.hostname : 'localhost');
  this.port = opts.port || (typeof location !== 'undefined' && location.port
      ? location.port
      : (this.secure ? 443 : 80));
  this.query = opts.query || {};
  if ('string' === typeof this.query) this.query = parseqs.decode(this.query);
  this.upgrade = false !== opts.upgrade;
  this.path = (opts.path || '/engine.io').replace(/\/$/, '') + '/';
  this.forceJSONP = !!opts.forceJSONP;
  this.jsonp = false !== opts.jsonp;
  this.forceBase64 = !!opts.forceBase64;
  this.enablesXDR = !!opts.enablesXDR;
  this.withCredentials = false !== opts.withCredentials;
  this.timestampParam = opts.timestampParam || 't';
  this.timestampRequests = opts.timestampRequests;
  this.transports = opts.transports || ['polling', 'websocket'];
  this.transportOptions = opts.transportOptions || {};
  this.readyState = '';
  this.writeBuffer = [];
  this.prevBufferLen = 0;
  this.policyPort = opts.policyPort || 843;
  this.rememberUpgrade = opts.rememberUpgrade || false;
  this.binaryType = null;
  this.onlyBinaryUpgrades = opts.onlyBinaryUpgrades;
  this.perMessageDeflate = false !== opts.perMessageDeflate ? (opts.perMessageDeflate || {}) : false;

  if (true === this.perMessageDeflate) this.perMessageDeflate = {};
  if (this.perMessageDeflate && null == this.perMessageDeflate.threshold) {
    this.perMessageDeflate.threshold = 1024;
  }

  // SSL options for Node.js client
  this.pfx = opts.pfx || null;
  this.key = opts.key || null;
  this.passphrase = opts.passphrase || null;
  this.cert = opts.cert || null;
  this.ca = opts.ca || null;
  this.ciphers = opts.ciphers || null;
  this.rejectUnauthorized = opts.rejectUnauthorized === undefined ? true : opts.rejectUnauthorized;
  this.forceNode = !!opts.forceNode;

  // detect ReactNative environment
  this.isReactNative = (typeof navigator !== 'undefined' && typeof navigator.product === 'string' && navigator.product.toLowerCase() === 'reactnative');

  // other options for Node.js or ReactNative client
  if (typeof self === 'undefined' || this.isReactNative) {
    if (opts.extraHeaders && Object.keys(opts.extraHeaders).length > 0) {
      this.extraHeaders = opts.extraHeaders;
    }

    if (opts.localAddress) {
      this.localAddress = opts.localAddress;
    }
  }

  // set on handshake
  this.id = null;
  this.upgrades = null;
  this.pingInterval = null;
  this.pingTimeout = null;

  // set on heartbeat
  this.pingIntervalTimer = null;
  this.pingTimeoutTimer = null;

  this.open();
}

Socket.priorWebsocketSuccess = false;

/**
 * Mix in `Emitter`.
 */

Emitter(Socket.prototype);

/**
 * Protocol version.
 *
 * @api public
 */

Socket.protocol = parser.protocol; // this is an int

/**
 * Expose deps for legacy compatibility
 * and standalone browser access.
 */

Socket.Socket = Socket;
Socket.Transport = __webpack_require__(/*! ./transport */ "./node_modules/engine.io-client/lib/transport.js");
Socket.transports = __webpack_require__(/*! ./transports/index */ "./node_modules/engine.io-client/lib/transports/index.js");
Socket.parser = __webpack_require__(/*! engine.io-parser */ "./node_modules/engine.io-parser/lib/browser.js");

/**
 * Creates transport of the given type.
 *
 * @param {String} transport name
 * @return {Transport}
 * @api private
 */

Socket.prototype.createTransport = function (name) {
  debug('creating transport "%s"', name);
  var query = clone(this.query);

  // append engine.io protocol identifier
  query.EIO = parser.protocol;

  // transport name
  query.transport = name;

  // per-transport options
  var options = this.transportOptions[name] || {};

  // session id if we already have one
  if (this.id) query.sid = this.id;

  var transport = new transports[name]({
    query: query,
    socket: this,
    agent: options.agent || this.agent,
    hostname: options.hostname || this.hostname,
    port: options.port || this.port,
    secure: options.secure || this.secure,
    path: options.path || this.path,
    forceJSONP: options.forceJSONP || this.forceJSONP,
    jsonp: options.jsonp || this.jsonp,
    forceBase64: options.forceBase64 || this.forceBase64,
    enablesXDR: options.enablesXDR || this.enablesXDR,
    withCredentials: options.withCredentials || this.withCredentials,
    timestampRequests: options.timestampRequests || this.timestampRequests,
    timestampParam: options.timestampParam || this.timestampParam,
    policyPort: options.policyPort || this.policyPort,
    pfx: options.pfx || this.pfx,
    key: options.key || this.key,
    passphrase: options.passphrase || this.passphrase,
    cert: options.cert || this.cert,
    ca: options.ca || this.ca,
    ciphers: options.ciphers || this.ciphers,
    rejectUnauthorized: options.rejectUnauthorized || this.rejectUnauthorized,
    perMessageDeflate: options.perMessageDeflate || this.perMessageDeflate,
    extraHeaders: options.extraHeaders || this.extraHeaders,
    forceNode: options.forceNode || this.forceNode,
    localAddress: options.localAddress || this.localAddress,
    requestTimeout: options.requestTimeout || this.requestTimeout,
    protocols: options.protocols || void (0),
    isReactNative: this.isReactNative
  });

  return transport;
};

function clone (obj) {
  var o = {};
  for (var i in obj) {
    if (obj.hasOwnProperty(i)) {
      o[i] = obj[i];
    }
  }
  return o;
}

/**
 * Initializes transport to use and starts probe.
 *
 * @api private
 */
Socket.prototype.open = function () {
  var transport;
  if (this.rememberUpgrade && Socket.priorWebsocketSuccess && this.transports.indexOf('websocket') !== -1) {
    transport = 'websocket';
  } else if (0 === this.transports.length) {
    // Emit error on next tick so it can be listened to
    var self = this;
    setTimeout(function () {
      self.emit('error', 'No transports available');
    }, 0);
    return;
  } else {
    transport = this.transports[0];
  }
  this.readyState = 'opening';

  // Retry with the next transport if the transport is disabled (jsonp: false)
  try {
    transport = this.createTransport(transport);
  } catch (e) {
    this.transports.shift();
    this.open();
    return;
  }

  transport.open();
  this.setTransport(transport);
};

/**
 * Sets the current transport. Disables the existing one (if any).
 *
 * @api private
 */

Socket.prototype.setTransport = function (transport) {
  debug('setting transport %s', transport.name);
  var self = this;

  if (this.transport) {
    debug('clearing existing transport %s', this.transport.name);
    this.transport.removeAllListeners();
  }

  // set up transport
  this.transport = transport;

  // set up transport listeners
  transport
  .on('drain', function () {
    self.onDrain();
  })
  .on('packet', function (packet) {
    self.onPacket(packet);
  })
  .on('error', function (e) {
    self.onError(e);
  })
  .on('close', function () {
    self.onClose('transport close');
  });
};

/**
 * Probes a transport.
 *
 * @param {String} transport name
 * @api private
 */

Socket.prototype.probe = function (name) {
  debug('probing transport "%s"', name);
  var transport = this.createTransport(name, { probe: 1 });
  var failed = false;
  var self = this;

  Socket.priorWebsocketSuccess = false;

  function onTransportOpen () {
    if (self.onlyBinaryUpgrades) {
      var upgradeLosesBinary = !this.supportsBinary && self.transport.supportsBinary;
      failed = failed || upgradeLosesBinary;
    }
    if (failed) return;

    debug('probe transport "%s" opened', name);
    transport.send([{ type: 'ping', data: 'probe' }]);
    transport.once('packet', function (msg) {
      if (failed) return;
      if ('pong' === msg.type && 'probe' === msg.data) {
        debug('probe transport "%s" pong', name);
        self.upgrading = true;
        self.emit('upgrading', transport);
        if (!transport) return;
        Socket.priorWebsocketSuccess = 'websocket' === transport.name;

        debug('pausing current transport "%s"', self.transport.name);
        self.transport.pause(function () {
          if (failed) return;
          if ('closed' === self.readyState) return;
          debug('changing transport and sending upgrade packet');

          cleanup();

          self.setTransport(transport);
          transport.send([{ type: 'upgrade' }]);
          self.emit('upgrade', transport);
          transport = null;
          self.upgrading = false;
          self.flush();
        });
      } else {
        debug('probe transport "%s" failed', name);
        var err = new Error('probe error');
        err.transport = transport.name;
        self.emit('upgradeError', err);
      }
    });
  }

  function freezeTransport () {
    if (failed) return;

    // Any callback called by transport should be ignored since now
    failed = true;

    cleanup();

    transport.close();
    transport = null;
  }

  // Handle any error that happens while probing
  function onerror (err) {
    var error = new Error('probe error: ' + err);
    error.transport = transport.name;

    freezeTransport();

    debug('probe transport "%s" failed because of error: %s', name, err);

    self.emit('upgradeError', error);
  }

  function onTransportClose () {
    onerror('transport closed');
  }

  // When the socket is closed while we're probing
  function onclose () {
    onerror('socket closed');
  }

  // When the socket is upgraded while we're probing
  function onupgrade (to) {
    if (transport && to.name !== transport.name) {
      debug('"%s" works - aborting "%s"', to.name, transport.name);
      freezeTransport();
    }
  }

  // Remove all listeners on the transport and on self
  function cleanup () {
    transport.removeListener('open', onTransportOpen);
    transport.removeListener('error', onerror);
    transport.removeListener('close', onTransportClose);
    self.removeListener('close', onclose);
    self.removeListener('upgrading', onupgrade);
  }

  transport.once('open', onTransportOpen);
  transport.once('error', onerror);
  transport.once('close', onTransportClose);

  this.once('close', onclose);
  this.once('upgrading', onupgrade);

  transport.open();
};

/**
 * Called when connection is deemed open.
 *
 * @api public
 */

Socket.prototype.onOpen = function () {
  debug('socket open');
  this.readyState = 'open';
  Socket.priorWebsocketSuccess = 'websocket' === this.transport.name;
  this.emit('open');
  this.flush();

  // we check for `readyState` in case an `open`
  // listener already closed the socket
  if ('open' === this.readyState && this.upgrade && this.transport.pause) {
    debug('starting upgrade probes');
    for (var i = 0, l = this.upgrades.length; i < l; i++) {
      this.probe(this.upgrades[i]);
    }
  }
};

/**
 * Handles a packet.
 *
 * @api private
 */

Socket.prototype.onPacket = function (packet) {
  if ('opening' === this.readyState || 'open' === this.readyState ||
      'closing' === this.readyState) {
    debug('socket receive: type "%s", data "%s"', packet.type, packet.data);

    this.emit('packet', packet);

    // Socket is live - any packet counts
    this.emit('heartbeat');

    switch (packet.type) {
      case 'open':
        this.onHandshake(JSON.parse(packet.data));
        break;

      case 'pong':
        this.setPing();
        this.emit('pong');
        break;

      case 'error':
        var err = new Error('server error');
        err.code = packet.data;
        this.onError(err);
        break;

      case 'message':
        this.emit('data', packet.data);
        this.emit('message', packet.data);
        break;
    }
  } else {
    debug('packet received with socket readyState "%s"', this.readyState);
  }
};

/**
 * Called upon handshake completion.
 *
 * @param {Object} handshake obj
 * @api private
 */

Socket.prototype.onHandshake = function (data) {
  this.emit('handshake', data);
  this.id = data.sid;
  this.transport.query.sid = data.sid;
  this.upgrades = this.filterUpgrades(data.upgrades);
  this.pingInterval = data.pingInterval;
  this.pingTimeout = data.pingTimeout;
  this.onOpen();
  // In case open handler closes socket
  if ('closed' === this.readyState) return;
  this.setPing();

  // Prolong liveness of socket on heartbeat
  this.removeListener('heartbeat', this.onHeartbeat);
  this.on('heartbeat', this.onHeartbeat);
};

/**
 * Resets ping timeout.
 *
 * @api private
 */

Socket.prototype.onHeartbeat = function (timeout) {
  clearTimeout(this.pingTimeoutTimer);
  var self = this;
  self.pingTimeoutTimer = setTimeout(function () {
    if ('closed' === self.readyState) return;
    self.onClose('ping timeout');
  }, timeout || (self.pingInterval + self.pingTimeout));
};

/**
 * Pings server every `this.pingInterval` and expects response
 * within `this.pingTimeout` or closes connection.
 *
 * @api private
 */

Socket.prototype.setPing = function () {
  var self = this;
  clearTimeout(self.pingIntervalTimer);
  self.pingIntervalTimer = setTimeout(function () {
    debug('writing ping packet - expecting pong within %sms', self.pingTimeout);
    self.ping();
    self.onHeartbeat(self.pingTimeout);
  }, self.pingInterval);
};

/**
* Sends a ping packet.
*
* @api private
*/

Socket.prototype.ping = function () {
  var self = this;
  this.sendPacket('ping', function () {
    self.emit('ping');
  });
};

/**
 * Called on `drain` event
 *
 * @api private
 */

Socket.prototype.onDrain = function () {
  this.writeBuffer.splice(0, this.prevBufferLen);

  // setting prevBufferLen = 0 is very important
  // for example, when upgrading, upgrade packet is sent over,
  // and a nonzero prevBufferLen could cause problems on `drain`
  this.prevBufferLen = 0;

  if (0 === this.writeBuffer.length) {
    this.emit('drain');
  } else {
    this.flush();
  }
};

/**
 * Flush write buffers.
 *
 * @api private
 */

Socket.prototype.flush = function () {
  if ('closed' !== this.readyState && this.transport.writable &&
    !this.upgrading && this.writeBuffer.length) {
    debug('flushing %d packets in socket', this.writeBuffer.length);
    this.transport.send(this.writeBuffer);
    // keep track of current length of writeBuffer
    // splice writeBuffer and callbackBuffer on `drain`
    this.prevBufferLen = this.writeBuffer.length;
    this.emit('flush');
  }
};

/**
 * Sends a message.
 *
 * @param {String} message.
 * @param {Function} callback function.
 * @param {Object} options.
 * @return {Socket} for chaining.
 * @api public
 */

Socket.prototype.write =
Socket.prototype.send = function (msg, options, fn) {
  this.sendPacket('message', msg, options, fn);
  return this;
};

/**
 * Sends a packet.
 *
 * @param {String} packet type.
 * @param {String} data.
 * @param {Object} options.
 * @param {Function} callback function.
 * @api private
 */

Socket.prototype.sendPacket = function (type, data, options, fn) {
  if ('function' === typeof data) {
    fn = data;
    data = undefined;
  }

  if ('function' === typeof options) {
    fn = options;
    options = null;
  }

  if ('closing' === this.readyState || 'closed' === this.readyState) {
    return;
  }

  options = options || {};
  options.compress = false !== options.compress;

  var packet = {
    type: type,
    data: data,
    options: options
  };
  this.emit('packetCreate', packet);
  this.writeBuffer.push(packet);
  if (fn) this.once('flush', fn);
  this.flush();
};

/**
 * Closes the connection.
 *
 * @api private
 */

Socket.prototype.close = function () {
  if ('opening' === this.readyState || 'open' === this.readyState) {
    this.readyState = 'closing';

    var self = this;

    if (this.writeBuffer.length) {
      this.once('drain', function () {
        if (this.upgrading) {
          waitForUpgrade();
        } else {
          close();
        }
      });
    } else if (this.upgrading) {
      waitForUpgrade();
    } else {
      close();
    }
  }

  function close () {
    self.onClose('forced close');
    debug('socket closing - telling transport to close');
    self.transport.close();
  }

  function cleanupAndClose () {
    self.removeListener('upgrade', cleanupAndClose);
    self.removeListener('upgradeError', cleanupAndClose);
    close();
  }

  function waitForUpgrade () {
    // wait for upgrade to finish since we can't send packets while pausing a transport
    self.once('upgrade', cleanupAndClose);
    self.once('upgradeError', cleanupAndClose);
  }

  return this;
};

/**
 * Called upon transport error
 *
 * @api private
 */

Socket.prototype.onError = function (err) {
  debug('socket error %j', err);
  Socket.priorWebsocketSuccess = false;
  this.emit('error', err);
  this.onClose('transport error', err);
};

/**
 * Called upon transport close.
 *
 * @api private
 */

Socket.prototype.onClose = function (reason, desc) {
  if ('opening' === this.readyState || 'open' === this.readyState || 'closing' === this.readyState) {
    debug('socket close with reason: "%s"', reason);
    var self = this;

    // clear timers
    clearTimeout(this.pingIntervalTimer);
    clearTimeout(this.pingTimeoutTimer);

    // stop event from firing again for transport
    this.transport.removeAllListeners('close');

    // ensure transport won't stay open
    this.transport.close();

    // ignore further transport communication
    this.transport.removeAllListeners();

    // set ready state
    this.readyState = 'closed';

    // clear session id
    this.id = null;

    // emit close event
    this.emit('close', reason, desc);

    // clean buffers after, so users can still
    // grab the buffers on `close` event
    self.writeBuffer = [];
    self.prevBufferLen = 0;
  }
};

/**
 * Filters upgrades, returning only those matching client transports.
 *
 * @param {Array} server upgrades
 * @api private
 *
 */

Socket.prototype.filterUpgrades = function (upgrades) {
  var filteredUpgrades = [];
  for (var i = 0, j = upgrades.length; i < j; i++) {
    if (~index(this.transports, upgrades[i])) filteredUpgrades.push(upgrades[i]);
  }
  return filteredUpgrades;
};


/***/ }),

/***/ "./node_modules/engine.io-client/lib/transport.js":
/*!********************************************************!*\
  !*** ./node_modules/engine.io-client/lib/transport.js ***!
  \********************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

/**
 * Module dependencies.
 */

var parser = __webpack_require__(/*! engine.io-parser */ "./node_modules/engine.io-parser/lib/browser.js");
var Emitter = __webpack_require__(/*! component-emitter */ "./node_modules/component-emitter/index.js");

/**
 * Module exports.
 */

module.exports = Transport;

/**
 * Transport abstract constructor.
 *
 * @param {Object} options.
 * @api private
 */

function Transport (opts) {
  this.path = opts.path;
  this.hostname = opts.hostname;
  this.port = opts.port;
  this.secure = opts.secure;
  this.query = opts.query;
  this.timestampParam = opts.timestampParam;
  this.timestampRequests = opts.timestampRequests;
  this.readyState = '';
  this.agent = opts.agent || false;
  this.socket = opts.socket;
  this.enablesXDR = opts.enablesXDR;
  this.withCredentials = opts.withCredentials;

  // SSL options for Node.js client
  this.pfx = opts.pfx;
  this.key = opts.key;
  this.passphrase = opts.passphrase;
  this.cert = opts.cert;
  this.ca = opts.ca;
  this.ciphers = opts.ciphers;
  this.rejectUnauthorized = opts.rejectUnauthorized;
  this.forceNode = opts.forceNode;

  // results of ReactNative environment detection
  this.isReactNative = opts.isReactNative;

  // other options for Node.js client
  this.extraHeaders = opts.extraHeaders;
  this.localAddress = opts.localAddress;
}

/**
 * Mix in `Emitter`.
 */

Emitter(Transport.prototype);

/**
 * Emits an error.
 *
 * @param {String} str
 * @return {Transport} for chaining
 * @api public
 */

Transport.prototype.onError = function (msg, desc) {
  var err = new Error(msg);
  err.type = 'TransportError';
  err.description = desc;
  this.emit('error', err);
  return this;
};

/**
 * Opens the transport.
 *
 * @api public
 */

Transport.prototype.open = function () {
  if ('closed' === this.readyState || '' === this.readyState) {
    this.readyState = 'opening';
    this.doOpen();
  }

  return this;
};

/**
 * Closes the transport.
 *
 * @api private
 */

Transport.prototype.close = function () {
  if ('opening' === this.readyState || 'open' === this.readyState) {
    this.doClose();
    this.onClose();
  }

  return this;
};

/**
 * Sends multiple packets.
 *
 * @param {Array} packets
 * @api private
 */

Transport.prototype.send = function (packets) {
  if ('open' === this.readyState) {
    this.write(packets);
  } else {
    throw new Error('Transport not open');
  }
};

/**
 * Called upon open
 *
 * @api private
 */

Transport.prototype.onOpen = function () {
  this.readyState = 'open';
  this.writable = true;
  this.emit('open');
};

/**
 * Called with data.
 *
 * @param {String} data
 * @api private
 */

Transport.prototype.onData = function (data) {
  var packet = parser.decodePacket(data, this.socket.binaryType);
  this.onPacket(packet);
};

/**
 * Called with a decoded packet.
 */

Transport.prototype.onPacket = function (packet) {
  this.emit('packet', packet);
};

/**
 * Called upon close.
 *
 * @api private
 */

Transport.prototype.onClose = function () {
  this.readyState = 'closed';
  this.emit('close');
};


/***/ }),

/***/ "./node_modules/engine.io-client/lib/transports/index.js":
/*!***************************************************************!*\
  !*** ./node_modules/engine.io-client/lib/transports/index.js ***!
  \***************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

/**
 * Module dependencies
 */

var XMLHttpRequest = __webpack_require__(/*! xmlhttprequest-ssl */ "./node_modules/engine.io-client/lib/xmlhttprequest.js");
var XHR = __webpack_require__(/*! ./polling-xhr */ "./node_modules/engine.io-client/lib/transports/polling-xhr.js");
var JSONP = __webpack_require__(/*! ./polling-jsonp */ "./node_modules/engine.io-client/lib/transports/polling-jsonp.js");
var websocket = __webpack_require__(/*! ./websocket */ "./node_modules/engine.io-client/lib/transports/websocket.js");

/**
 * Export transports.
 */

exports.polling = polling;
exports.websocket = websocket;

/**
 * Polling transport polymorphic constructor.
 * Decides on xhr vs jsonp based on feature detection.
 *
 * @api private
 */

function polling (opts) {
  var xhr;
  var xd = false;
  var xs = false;
  var jsonp = false !== opts.jsonp;

  if (typeof location !== 'undefined') {
    var isSSL = 'https:' === location.protocol;
    var port = location.port;

    // some user agents have empty `location.port`
    if (!port) {
      port = isSSL ? 443 : 80;
    }

    xd = opts.hostname !== location.hostname || port !== opts.port;
    xs = opts.secure !== isSSL;
  }

  opts.xdomain = xd;
  opts.xscheme = xs;
  xhr = new XMLHttpRequest(opts);

  if ('open' in xhr && !opts.forceJSONP) {
    return new XHR(opts);
  } else {
    if (!jsonp) throw new Error('JSONP disabled');
    return new JSONP(opts);
  }
}


/***/ }),

/***/ "./node_modules/engine.io-client/lib/transports/polling-jsonp.js":
/*!***********************************************************************!*\
  !*** ./node_modules/engine.io-client/lib/transports/polling-jsonp.js ***!
  \***********************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

/* WEBPACK VAR INJECTION */(function(global) {/**
 * Module requirements.
 */

var Polling = __webpack_require__(/*! ./polling */ "./node_modules/engine.io-client/lib/transports/polling.js");
var inherit = __webpack_require__(/*! component-inherit */ "./node_modules/component-inherit/index.js");

/**
 * Module exports.
 */

module.exports = JSONPPolling;

/**
 * Cached regular expressions.
 */

var rNewline = /\n/g;
var rEscapedNewline = /\\n/g;

/**
 * Global JSONP callbacks.
 */

var callbacks;

/**
 * Noop.
 */

function empty () { }

/**
 * Until https://github.com/tc39/proposal-global is shipped.
 */
function glob () {
  return typeof self !== 'undefined' ? self
      : typeof window !== 'undefined' ? window
      : typeof global !== 'undefined' ? global : {};
}

/**
 * JSONP Polling constructor.
 *
 * @param {Object} opts.
 * @api public
 */

function JSONPPolling (opts) {
  Polling.call(this, opts);

  this.query = this.query || {};

  // define global callbacks array if not present
  // we do this here (lazily) to avoid unneeded global pollution
  if (!callbacks) {
    // we need to consider multiple engines in the same page
    var global = glob();
    callbacks = global.___eio = (global.___eio || []);
  }

  // callback identifier
  this.index = callbacks.length;

  // add callback to jsonp global
  var self = this;
  callbacks.push(function (msg) {
    self.onData(msg);
  });

  // append to query string
  this.query.j = this.index;

  // prevent spurious errors from being emitted when the window is unloaded
  if (typeof addEventListener === 'function') {
    addEventListener('beforeunload', function () {
      if (self.script) self.script.onerror = empty;
    }, false);
  }
}

/**
 * Inherits from Polling.
 */

inherit(JSONPPolling, Polling);

/*
 * JSONP only supports binary as base64 encoded strings
 */

JSONPPolling.prototype.supportsBinary = false;

/**
 * Closes the socket.
 *
 * @api private
 */

JSONPPolling.prototype.doClose = function () {
  if (this.script) {
    this.script.parentNode.removeChild(this.script);
    this.script = null;
  }

  if (this.form) {
    this.form.parentNode.removeChild(this.form);
    this.form = null;
    this.iframe = null;
  }

  Polling.prototype.doClose.call(this);
};

/**
 * Starts a poll cycle.
 *
 * @api private
 */

JSONPPolling.prototype.doPoll = function () {
  var self = this;
  var script = document.createElement('script');

  if (this.script) {
    this.script.parentNode.removeChild(this.script);
    this.script = null;
  }

  script.async = true;
  script.src = this.uri();
  script.onerror = function (e) {
    self.onError('jsonp poll error', e);
  };

  var insertAt = document.getElementsByTagName('script')[0];
  if (insertAt) {
    insertAt.parentNode.insertBefore(script, insertAt);
  } else {
    (document.head || document.body).appendChild(script);
  }
  this.script = script;

  var isUAgecko = 'undefined' !== typeof navigator && /gecko/i.test(navigator.userAgent);

  if (isUAgecko) {
    setTimeout(function () {
      var iframe = document.createElement('iframe');
      document.body.appendChild(iframe);
      document.body.removeChild(iframe);
    }, 100);
  }
};

/**
 * Writes with a hidden iframe.
 *
 * @param {String} data to send
 * @param {Function} called upon flush.
 * @api private
 */

JSONPPolling.prototype.doWrite = function (data, fn) {
  var self = this;

  if (!this.form) {
    var form = document.createElement('form');
    var area = document.createElement('textarea');
    var id = this.iframeId = 'eio_iframe_' + this.index;
    var iframe;

    form.className = 'socketio';
    form.style.position = 'absolute';
    form.style.top = '-1000px';
    form.style.left = '-1000px';
    form.target = id;
    form.method = 'POST';
    form.setAttribute('accept-charset', 'utf-8');
    area.name = 'd';
    form.appendChild(area);
    document.body.appendChild(form);

    this.form = form;
    this.area = area;
  }

  this.form.action = this.uri();

  function complete () {
    initIframe();
    fn();
  }

  function initIframe () {
    if (self.iframe) {
      try {
        self.form.removeChild(self.iframe);
      } catch (e) {
        self.onError('jsonp polling iframe removal error', e);
      }
    }

    try {
      // ie6 dynamic iframes with target="" support (thanks Chris Lambacher)
      var html = '<iframe src="javascript:0" name="' + self.iframeId + '">';
      iframe = document.createElement(html);
    } catch (e) {
      iframe = document.createElement('iframe');
      iframe.name = self.iframeId;
      iframe.src = 'javascript:0';
    }

    iframe.id = self.iframeId;

    self.form.appendChild(iframe);
    self.iframe = iframe;
  }

  initIframe();

  // escape \n to prevent it from being converted into \r\n by some UAs
  // double escaping is required for escaped new lines because unescaping of new lines can be done safely on server-side
  data = data.replace(rEscapedNewline, '\\\n');
  this.area.value = data.replace(rNewline, '\\n');

  try {
    this.form.submit();
  } catch (e) {}

  if (this.iframe.attachEvent) {
    this.iframe.onreadystatechange = function () {
      if (self.iframe.readyState === 'complete') {
        complete();
      }
    };
  } else {
    this.iframe.onload = complete;
  }
};

/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./../../../webpack/buildin/global.js */ "./node_modules/webpack/buildin/global.js")))

/***/ }),

/***/ "./node_modules/engine.io-client/lib/transports/polling-xhr.js":
/*!*********************************************************************!*\
  !*** ./node_modules/engine.io-client/lib/transports/polling-xhr.js ***!
  \*********************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

/* global attachEvent */

/**
 * Module requirements.
 */

var XMLHttpRequest = __webpack_require__(/*! xmlhttprequest-ssl */ "./node_modules/engine.io-client/lib/xmlhttprequest.js");
var Polling = __webpack_require__(/*! ./polling */ "./node_modules/engine.io-client/lib/transports/polling.js");
var Emitter = __webpack_require__(/*! component-emitter */ "./node_modules/component-emitter/index.js");
var inherit = __webpack_require__(/*! component-inherit */ "./node_modules/component-inherit/index.js");
var debug = __webpack_require__(/*! debug */ "./node_modules/debug/src/browser.js")('engine.io-client:polling-xhr');

/**
 * Module exports.
 */

module.exports = XHR;
module.exports.Request = Request;

/**
 * Empty function
 */

function empty () {}

/**
 * XHR Polling constructor.
 *
 * @param {Object} opts
 * @api public
 */

function XHR (opts) {
  Polling.call(this, opts);
  this.requestTimeout = opts.requestTimeout;
  this.extraHeaders = opts.extraHeaders;

  if (typeof location !== 'undefined') {
    var isSSL = 'https:' === location.protocol;
    var port = location.port;

    // some user agents have empty `location.port`
    if (!port) {
      port = isSSL ? 443 : 80;
    }

    this.xd = (typeof location !== 'undefined' && opts.hostname !== location.hostname) ||
      port !== opts.port;
    this.xs = opts.secure !== isSSL;
  }
}

/**
 * Inherits from Polling.
 */

inherit(XHR, Polling);

/**
 * XHR supports binary
 */

XHR.prototype.supportsBinary = true;

/**
 * Creates a request.
 *
 * @param {String} method
 * @api private
 */

XHR.prototype.request = function (opts) {
  opts = opts || {};
  opts.uri = this.uri();
  opts.xd = this.xd;
  opts.xs = this.xs;
  opts.agent = this.agent || false;
  opts.supportsBinary = this.supportsBinary;
  opts.enablesXDR = this.enablesXDR;
  opts.withCredentials = this.withCredentials;

  // SSL options for Node.js client
  opts.pfx = this.pfx;
  opts.key = this.key;
  opts.passphrase = this.passphrase;
  opts.cert = this.cert;
  opts.ca = this.ca;
  opts.ciphers = this.ciphers;
  opts.rejectUnauthorized = this.rejectUnauthorized;
  opts.requestTimeout = this.requestTimeout;

  // other options for Node.js client
  opts.extraHeaders = this.extraHeaders;

  return new Request(opts);
};

/**
 * Sends data.
 *
 * @param {String} data to send.
 * @param {Function} called upon flush.
 * @api private
 */

XHR.prototype.doWrite = function (data, fn) {
  var isBinary = typeof data !== 'string' && data !== undefined;
  var req = this.request({ method: 'POST', data: data, isBinary: isBinary });
  var self = this;
  req.on('success', fn);
  req.on('error', function (err) {
    self.onError('xhr post error', err);
  });
  this.sendXhr = req;
};

/**
 * Starts a poll cycle.
 *
 * @api private
 */

XHR.prototype.doPoll = function () {
  debug('xhr poll');
  var req = this.request();
  var self = this;
  req.on('data', function (data) {
    self.onData(data);
  });
  req.on('error', function (err) {
    self.onError('xhr poll error', err);
  });
  this.pollXhr = req;
};

/**
 * Request constructor
 *
 * @param {Object} options
 * @api public
 */

function Request (opts) {
  this.method = opts.method || 'GET';
  this.uri = opts.uri;
  this.xd = !!opts.xd;
  this.xs = !!opts.xs;
  this.async = false !== opts.async;
  this.data = undefined !== opts.data ? opts.data : null;
  this.agent = opts.agent;
  this.isBinary = opts.isBinary;
  this.supportsBinary = opts.supportsBinary;
  this.enablesXDR = opts.enablesXDR;
  this.withCredentials = opts.withCredentials;
  this.requestTimeout = opts.requestTimeout;

  // SSL options for Node.js client
  this.pfx = opts.pfx;
  this.key = opts.key;
  this.passphrase = opts.passphrase;
  this.cert = opts.cert;
  this.ca = opts.ca;
  this.ciphers = opts.ciphers;
  this.rejectUnauthorized = opts.rejectUnauthorized;

  // other options for Node.js client
  this.extraHeaders = opts.extraHeaders;

  this.create();
}

/**
 * Mix in `Emitter`.
 */

Emitter(Request.prototype);

/**
 * Creates the XHR object and sends the request.
 *
 * @api private
 */

Request.prototype.create = function () {
  var opts = { agent: this.agent, xdomain: this.xd, xscheme: this.xs, enablesXDR: this.enablesXDR };

  // SSL options for Node.js client
  opts.pfx = this.pfx;
  opts.key = this.key;
  opts.passphrase = this.passphrase;
  opts.cert = this.cert;
  opts.ca = this.ca;
  opts.ciphers = this.ciphers;
  opts.rejectUnauthorized = this.rejectUnauthorized;

  var xhr = this.xhr = new XMLHttpRequest(opts);
  var self = this;

  try {
    debug('xhr open %s: %s', this.method, this.uri);
    xhr.open(this.method, this.uri, this.async);
    try {
      if (this.extraHeaders) {
        xhr.setDisableHeaderCheck && xhr.setDisableHeaderCheck(true);
        for (var i in this.extraHeaders) {
          if (this.extraHeaders.hasOwnProperty(i)) {
            xhr.setRequestHeader(i, this.extraHeaders[i]);
          }
        }
      }
    } catch (e) {}

    if ('POST' === this.method) {
      try {
        if (this.isBinary) {
          xhr.setRequestHeader('Content-type', 'application/octet-stream');
        } else {
          xhr.setRequestHeader('Content-type', 'text/plain;charset=UTF-8');
        }
      } catch (e) {}
    }

    try {
      xhr.setRequestHeader('Accept', '*/*');
    } catch (e) {}

    // ie6 check
    if ('withCredentials' in xhr) {
      xhr.withCredentials = this.withCredentials;
    }

    if (this.requestTimeout) {
      xhr.timeout = this.requestTimeout;
    }

    if (this.hasXDR()) {
      xhr.onload = function () {
        self.onLoad();
      };
      xhr.onerror = function () {
        self.onError(xhr.responseText);
      };
    } else {
      xhr.onreadystatechange = function () {
        if (xhr.readyState === 2) {
          try {
            var contentType = xhr.getResponseHeader('Content-Type');
            if (self.supportsBinary && contentType === 'application/octet-stream' || contentType === 'application/octet-stream; charset=UTF-8') {
              xhr.responseType = 'arraybuffer';
            }
          } catch (e) {}
        }
        if (4 !== xhr.readyState) return;
        if (200 === xhr.status || 1223 === xhr.status) {
          self.onLoad();
        } else {
          // make sure the `error` event handler that's user-set
          // does not throw in the same tick and gets caught here
          setTimeout(function () {
            self.onError(typeof xhr.status === 'number' ? xhr.status : 0);
          }, 0);
        }
      };
    }

    debug('xhr data %s', this.data);
    xhr.send(this.data);
  } catch (e) {
    // Need to defer since .create() is called directly fhrom the constructor
    // and thus the 'error' event can only be only bound *after* this exception
    // occurs.  Therefore, also, we cannot throw here at all.
    setTimeout(function () {
      self.onError(e);
    }, 0);
    return;
  }

  if (typeof document !== 'undefined') {
    this.index = Request.requestsCount++;
    Request.requests[this.index] = this;
  }
};

/**
 * Called upon successful response.
 *
 * @api private
 */

Request.prototype.onSuccess = function () {
  this.emit('success');
  this.cleanup();
};

/**
 * Called if we have data.
 *
 * @api private
 */

Request.prototype.onData = function (data) {
  this.emit('data', data);
  this.onSuccess();
};

/**
 * Called upon error.
 *
 * @api private
 */

Request.prototype.onError = function (err) {
  this.emit('error', err);
  this.cleanup(true);
};

/**
 * Cleans up house.
 *
 * @api private
 */

Request.prototype.cleanup = function (fromError) {
  if ('undefined' === typeof this.xhr || null === this.xhr) {
    return;
  }
  // xmlhttprequest
  if (this.hasXDR()) {
    this.xhr.onload = this.xhr.onerror = empty;
  } else {
    this.xhr.onreadystatechange = empty;
  }

  if (fromError) {
    try {
      this.xhr.abort();
    } catch (e) {}
  }

  if (typeof document !== 'undefined') {
    delete Request.requests[this.index];
  }

  this.xhr = null;
};

/**
 * Called upon load.
 *
 * @api private
 */

Request.prototype.onLoad = function () {
  var data;
  try {
    var contentType;
    try {
      contentType = this.xhr.getResponseHeader('Content-Type');
    } catch (e) {}
    if (contentType === 'application/octet-stream' || contentType === 'application/octet-stream; charset=UTF-8') {
      data = this.xhr.response || this.xhr.responseText;
    } else {
      data = this.xhr.responseText;
    }
  } catch (e) {
    this.onError(e);
  }
  if (null != data) {
    this.onData(data);
  }
};

/**
 * Check if it has XDomainRequest.
 *
 * @api private
 */

Request.prototype.hasXDR = function () {
  return typeof XDomainRequest !== 'undefined' && !this.xs && this.enablesXDR;
};

/**
 * Aborts the request.
 *
 * @api public
 */

Request.prototype.abort = function () {
  this.cleanup();
};

/**
 * Aborts pending requests when unloading the window. This is needed to prevent
 * memory leaks (e.g. when using IE) and to ensure that no spurious error is
 * emitted.
 */

Request.requestsCount = 0;
Request.requests = {};

if (typeof document !== 'undefined') {
  if (typeof attachEvent === 'function') {
    attachEvent('onunload', unloadHandler);
  } else if (typeof addEventListener === 'function') {
    var terminationEvent = 'onpagehide' in self ? 'pagehide' : 'unload';
    addEventListener(terminationEvent, unloadHandler, false);
  }
}

function unloadHandler () {
  for (var i in Request.requests) {
    if (Request.requests.hasOwnProperty(i)) {
      Request.requests[i].abort();
    }
  }
}


/***/ }),

/***/ "./node_modules/engine.io-client/lib/transports/polling.js":
/*!*****************************************************************!*\
  !*** ./node_modules/engine.io-client/lib/transports/polling.js ***!
  \*****************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

/**
 * Module dependencies.
 */

var Transport = __webpack_require__(/*! ../transport */ "./node_modules/engine.io-client/lib/transport.js");
var parseqs = __webpack_require__(/*! parseqs */ "./node_modules/parseqs/index.js");
var parser = __webpack_require__(/*! engine.io-parser */ "./node_modules/engine.io-parser/lib/browser.js");
var inherit = __webpack_require__(/*! component-inherit */ "./node_modules/component-inherit/index.js");
var yeast = __webpack_require__(/*! yeast */ "./node_modules/yeast/index.js");
var debug = __webpack_require__(/*! debug */ "./node_modules/debug/src/browser.js")('engine.io-client:polling');

/**
 * Module exports.
 */

module.exports = Polling;

/**
 * Is XHR2 supported?
 */

var hasXHR2 = (function () {
  var XMLHttpRequest = __webpack_require__(/*! xmlhttprequest-ssl */ "./node_modules/engine.io-client/lib/xmlhttprequest.js");
  var xhr = new XMLHttpRequest({ xdomain: false });
  return null != xhr.responseType;
})();

/**
 * Polling interface.
 *
 * @param {Object} opts
 * @api private
 */

function Polling (opts) {
  var forceBase64 = (opts && opts.forceBase64);
  if (!hasXHR2 || forceBase64) {
    this.supportsBinary = false;
  }
  Transport.call(this, opts);
}

/**
 * Inherits from Transport.
 */

inherit(Polling, Transport);

/**
 * Transport name.
 */

Polling.prototype.name = 'polling';

/**
 * Opens the socket (triggers polling). We write a PING message to determine
 * when the transport is open.
 *
 * @api private
 */

Polling.prototype.doOpen = function () {
  this.poll();
};

/**
 * Pauses polling.
 *
 * @param {Function} callback upon buffers are flushed and transport is paused
 * @api private
 */

Polling.prototype.pause = function (onPause) {
  var self = this;

  this.readyState = 'pausing';

  function pause () {
    debug('paused');
    self.readyState = 'paused';
    onPause();
  }

  if (this.polling || !this.writable) {
    var total = 0;

    if (this.polling) {
      debug('we are currently polling - waiting to pause');
      total++;
      this.once('pollComplete', function () {
        debug('pre-pause polling complete');
        --total || pause();
      });
    }

    if (!this.writable) {
      debug('we are currently writing - waiting to pause');
      total++;
      this.once('drain', function () {
        debug('pre-pause writing complete');
        --total || pause();
      });
    }
  } else {
    pause();
  }
};

/**
 * Starts polling cycle.
 *
 * @api public
 */

Polling.prototype.poll = function () {
  debug('polling');
  this.polling = true;
  this.doPoll();
  this.emit('poll');
};

/**
 * Overloads onData to detect payloads.
 *
 * @api private
 */

Polling.prototype.onData = function (data) {
  var self = this;
  debug('polling got data %s', data);
  var callback = function (packet, index, total) {
    // if its the first message we consider the transport open
    if ('opening' === self.readyState) {
      self.onOpen();
    }

    // if its a close packet, we close the ongoing requests
    if ('close' === packet.type) {
      self.onClose();
      return false;
    }

    // otherwise bypass onData and handle the message
    self.onPacket(packet);
  };

  // decode payload
  parser.decodePayload(data, this.socket.binaryType, callback);

  // if an event did not trigger closing
  if ('closed' !== this.readyState) {
    // if we got data we're not polling
    this.polling = false;
    this.emit('pollComplete');

    if ('open' === this.readyState) {
      this.poll();
    } else {
      debug('ignoring poll - transport state "%s"', this.readyState);
    }
  }
};

/**
 * For polling, send a close packet.
 *
 * @api private
 */

Polling.prototype.doClose = function () {
  var self = this;

  function close () {
    debug('writing close packet');
    self.write([{ type: 'close' }]);
  }

  if ('open' === this.readyState) {
    debug('transport open - closing');
    close();
  } else {
    // in case we're trying to close while
    // handshaking is in progress (GH-164)
    debug('transport not open - deferring close');
    this.once('open', close);
  }
};

/**
 * Writes a packets payload.
 *
 * @param {Array} data packets
 * @param {Function} drain callback
 * @api private
 */

Polling.prototype.write = function (packets) {
  var self = this;
  this.writable = false;
  var callbackfn = function () {
    self.writable = true;
    self.emit('drain');
  };

  parser.encodePayload(packets, this.supportsBinary, function (data) {
    self.doWrite(data, callbackfn);
  });
};

/**
 * Generates uri for connection.
 *
 * @api private
 */

Polling.prototype.uri = function () {
  var query = this.query || {};
  var schema = this.secure ? 'https' : 'http';
  var port = '';

  // cache busting is forced
  if (false !== this.timestampRequests) {
    query[this.timestampParam] = yeast();
  }

  if (!this.supportsBinary && !query.sid) {
    query.b64 = 1;
  }

  query = parseqs.encode(query);

  // avoid port if default for schema
  if (this.port && (('https' === schema && Number(this.port) !== 443) ||
     ('http' === schema && Number(this.port) !== 80))) {
    port = ':' + this.port;
  }

  // prepend ? to query
  if (query.length) {
    query = '?' + query;
  }

  var ipv6 = this.hostname.indexOf(':') !== -1;
  return schema + '://' + (ipv6 ? '[' + this.hostname + ']' : this.hostname) + port + this.path + query;
};


/***/ }),

/***/ "./node_modules/engine.io-client/lib/transports/websocket.js":
/*!*******************************************************************!*\
  !*** ./node_modules/engine.io-client/lib/transports/websocket.js ***!
  \*******************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

/* WEBPACK VAR INJECTION */(function(Buffer) {/**
 * Module dependencies.
 */

var Transport = __webpack_require__(/*! ../transport */ "./node_modules/engine.io-client/lib/transport.js");
var parser = __webpack_require__(/*! engine.io-parser */ "./node_modules/engine.io-parser/lib/browser.js");
var parseqs = __webpack_require__(/*! parseqs */ "./node_modules/parseqs/index.js");
var inherit = __webpack_require__(/*! component-inherit */ "./node_modules/component-inherit/index.js");
var yeast = __webpack_require__(/*! yeast */ "./node_modules/yeast/index.js");
var debug = __webpack_require__(/*! debug */ "./node_modules/debug/src/browser.js")('engine.io-client:websocket');

var BrowserWebSocket, NodeWebSocket;

if (typeof WebSocket !== 'undefined') {
  BrowserWebSocket = WebSocket;
} else if (typeof self !== 'undefined') {
  BrowserWebSocket = self.WebSocket || self.MozWebSocket;
}

if (typeof window === 'undefined') {
  try {
    NodeWebSocket = __webpack_require__(/*! ws */ 0);
  } catch (e) { }
}

/**
 * Get either the `WebSocket` or `MozWebSocket` globals
 * in the browser or try to resolve WebSocket-compatible
 * interface exposed by `ws` for Node-like environment.
 */

var WebSocketImpl = BrowserWebSocket || NodeWebSocket;

/**
 * Module exports.
 */

module.exports = WS;

/**
 * WebSocket transport constructor.
 *
 * @api {Object} connection options
 * @api public
 */

function WS (opts) {
  var forceBase64 = (opts && opts.forceBase64);
  if (forceBase64) {
    this.supportsBinary = false;
  }
  this.perMessageDeflate = opts.perMessageDeflate;
  this.usingBrowserWebSocket = BrowserWebSocket && !opts.forceNode;
  this.protocols = opts.protocols;
  if (!this.usingBrowserWebSocket) {
    WebSocketImpl = NodeWebSocket;
  }
  Transport.call(this, opts);
}

/**
 * Inherits from Transport.
 */

inherit(WS, Transport);

/**
 * Transport name.
 *
 * @api public
 */

WS.prototype.name = 'websocket';

/*
 * WebSockets support binary
 */

WS.prototype.supportsBinary = true;

/**
 * Opens socket.
 *
 * @api private
 */

WS.prototype.doOpen = function () {
  if (!this.check()) {
    // let probe timeout
    return;
  }

  var uri = this.uri();
  var protocols = this.protocols;
  var opts = {
    agent: this.agent,
    perMessageDeflate: this.perMessageDeflate
  };

  // SSL options for Node.js client
  opts.pfx = this.pfx;
  opts.key = this.key;
  opts.passphrase = this.passphrase;
  opts.cert = this.cert;
  opts.ca = this.ca;
  opts.ciphers = this.ciphers;
  opts.rejectUnauthorized = this.rejectUnauthorized;
  if (this.extraHeaders) {
    opts.headers = this.extraHeaders;
  }
  if (this.localAddress) {
    opts.localAddress = this.localAddress;
  }

  try {
    this.ws =
      this.usingBrowserWebSocket && !this.isReactNative
        ? protocols
          ? new WebSocketImpl(uri, protocols)
          : new WebSocketImpl(uri)
        : new WebSocketImpl(uri, protocols, opts);
  } catch (err) {
    return this.emit('error', err);
  }

  if (this.ws.binaryType === undefined) {
    this.supportsBinary = false;
  }

  if (this.ws.supports && this.ws.supports.binary) {
    this.supportsBinary = true;
    this.ws.binaryType = 'nodebuffer';
  } else {
    this.ws.binaryType = 'arraybuffer';
  }

  this.addEventListeners();
};

/**
 * Adds event listeners to the socket
 *
 * @api private
 */

WS.prototype.addEventListeners = function () {
  var self = this;

  this.ws.onopen = function () {
    self.onOpen();
  };
  this.ws.onclose = function () {
    self.onClose();
  };
  this.ws.onmessage = function (ev) {
    self.onData(ev.data);
  };
  this.ws.onerror = function (e) {
    self.onError('websocket error', e);
  };
};

/**
 * Writes data to socket.
 *
 * @param {Array} array of packets.
 * @api private
 */

WS.prototype.write = function (packets) {
  var self = this;
  this.writable = false;

  // encodePacket efficient as it uses WS framing
  // no need for encodePayload
  var total = packets.length;
  for (var i = 0, l = total; i < l; i++) {
    (function (packet) {
      parser.encodePacket(packet, self.supportsBinary, function (data) {
        if (!self.usingBrowserWebSocket) {
          // always create a new object (GH-437)
          var opts = {};
          if (packet.options) {
            opts.compress = packet.options.compress;
          }

          if (self.perMessageDeflate) {
            var len = 'string' === typeof data ? Buffer.byteLength(data) : data.length;
            if (len < self.perMessageDeflate.threshold) {
              opts.compress = false;
            }
          }
        }

        // Sometimes the websocket has already been closed but the browser didn't
        // have a chance of informing us about it yet, in that case send will
        // throw an error
        try {
          if (self.usingBrowserWebSocket) {
            // TypeError is thrown when passing the second argument on Safari
            self.ws.send(data);
          } else {
            self.ws.send(data, opts);
          }
        } catch (e) {
          debug('websocket closed before onclose event');
        }

        --total || done();
      });
    })(packets[i]);
  }

  function done () {
    self.emit('flush');

    // fake drain
    // defer to next tick to allow Socket to clear writeBuffer
    setTimeout(function () {
      self.writable = true;
      self.emit('drain');
    }, 0);
  }
};

/**
 * Called upon close
 *
 * @api private
 */

WS.prototype.onClose = function () {
  Transport.prototype.onClose.call(this);
};

/**
 * Closes socket.
 *
 * @api private
 */

WS.prototype.doClose = function () {
  if (typeof this.ws !== 'undefined') {
    this.ws.close();
  }
};

/**
 * Generates uri for connection.
 *
 * @api private
 */

WS.prototype.uri = function () {
  var query = this.query || {};
  var schema = this.secure ? 'wss' : 'ws';
  var port = '';

  // avoid port if default for schema
  if (this.port && (('wss' === schema && Number(this.port) !== 443) ||
    ('ws' === schema && Number(this.port) !== 80))) {
    port = ':' + this.port;
  }

  // append timestamp to URI
  if (this.timestampRequests) {
    query[this.timestampParam] = yeast();
  }

  // communicate binary support capabilities
  if (!this.supportsBinary) {
    query.b64 = 1;
  }

  query = parseqs.encode(query);

  // prepend ? to query
  if (query.length) {
    query = '?' + query;
  }

  var ipv6 = this.hostname.indexOf(':') !== -1;
  return schema + '://' + (ipv6 ? '[' + this.hostname + ']' : this.hostname) + port + this.path + query;
};

/**
 * Feature detection for WebSocket.
 *
 * @return {Boolean} whether this transport is available.
 * @api public
 */

WS.prototype.check = function () {
  return !!WebSocketImpl && !('__initialize' in WebSocketImpl && this.name === WS.prototype.name);
};

/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./../../../buffer/index.js */ "./node_modules/buffer/index.js").Buffer))

/***/ }),

/***/ "./node_modules/engine.io-client/lib/xmlhttprequest.js":
/*!*************************************************************!*\
  !*** ./node_modules/engine.io-client/lib/xmlhttprequest.js ***!
  \*************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

// browser shim for xmlhttprequest module

var hasCORS = __webpack_require__(/*! has-cors */ "./node_modules/has-cors/index.js");

module.exports = function (opts) {
  var xdomain = opts.xdomain;

  // scheme must be same when usign XDomainRequest
  // http://blogs.msdn.com/b/ieinternals/archive/2010/05/13/xdomainrequest-restrictions-limitations-and-workarounds.aspx
  var xscheme = opts.xscheme;

  // XDomainRequest has a flow of not sending cookie, therefore it should be disabled as a default.
  // https://github.com/Automattic/engine.io-client/pull/217
  var enablesXDR = opts.enablesXDR;

  // XMLHttpRequest can be disabled on IE
  try {
    if ('undefined' !== typeof XMLHttpRequest && (!xdomain || hasCORS)) {
      return new XMLHttpRequest();
    }
  } catch (e) { }

  // Use XDomainRequest for IE8 if enablesXDR is true
  // because loading bar keeps flashing when using jsonp-polling
  // https://github.com/yujiosaka/socke.io-ie8-loading-example
  try {
    if ('undefined' !== typeof XDomainRequest && !xscheme && enablesXDR) {
      return new XDomainRequest();
    }
  } catch (e) { }

  if (!xdomain) {
    try {
      return new self[['Active'].concat('Object').join('X')]('Microsoft.XMLHTTP');
    } catch (e) { }
  }
};


/***/ }),

/***/ "./node_modules/engine.io-parser/lib/browser.js":
/*!******************************************************!*\
  !*** ./node_modules/engine.io-parser/lib/browser.js ***!
  \******************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

/**
 * Module dependencies.
 */

var keys = __webpack_require__(/*! ./keys */ "./node_modules/engine.io-parser/lib/keys.js");
var hasBinary = __webpack_require__(/*! has-binary2 */ "./node_modules/has-binary2/index.js");
var sliceBuffer = __webpack_require__(/*! arraybuffer.slice */ "./node_modules/arraybuffer.slice/index.js");
var after = __webpack_require__(/*! after */ "./node_modules/after/index.js");
var utf8 = __webpack_require__(/*! ./utf8 */ "./node_modules/engine.io-parser/lib/utf8.js");

var base64encoder;
if (typeof ArrayBuffer !== 'undefined') {
  base64encoder = __webpack_require__(/*! base64-arraybuffer */ "./node_modules/base64-arraybuffer/lib/base64-arraybuffer.js");
}

/**
 * Check if we are running an android browser. That requires us to use
 * ArrayBuffer with polling transports...
 *
 * http://ghinda.net/jpeg-blob-ajax-android/
 */

var isAndroid = typeof navigator !== 'undefined' && /Android/i.test(navigator.userAgent);

/**
 * Check if we are running in PhantomJS.
 * Uploading a Blob with PhantomJS does not work correctly, as reported here:
 * https://github.com/ariya/phantomjs/issues/11395
 * @type boolean
 */
var isPhantomJS = typeof navigator !== 'undefined' && /PhantomJS/i.test(navigator.userAgent);

/**
 * When true, avoids using Blobs to encode payloads.
 * @type boolean
 */
var dontSendBlobs = isAndroid || isPhantomJS;

/**
 * Current protocol version.
 */

exports.protocol = 3;

/**
 * Packet types.
 */

var packets = exports.packets = {
    open:     0    // non-ws
  , close:    1    // non-ws
  , ping:     2
  , pong:     3
  , message:  4
  , upgrade:  5
  , noop:     6
};

var packetslist = keys(packets);

/**
 * Premade error packet.
 */

var err = { type: 'error', data: 'parser error' };

/**
 * Create a blob api even for blob builder when vendor prefixes exist
 */

var Blob = __webpack_require__(/*! blob */ "./node_modules/blob/index.js");

/**
 * Encodes a packet.
 *
 *     <packet type id> [ <data> ]
 *
 * Example:
 *
 *     5hello world
 *     3
 *     4
 *
 * Binary is encoded in an identical principle
 *
 * @api private
 */

exports.encodePacket = function (packet, supportsBinary, utf8encode, callback) {
  if (typeof supportsBinary === 'function') {
    callback = supportsBinary;
    supportsBinary = false;
  }

  if (typeof utf8encode === 'function') {
    callback = utf8encode;
    utf8encode = null;
  }

  var data = (packet.data === undefined)
    ? undefined
    : packet.data.buffer || packet.data;

  if (typeof ArrayBuffer !== 'undefined' && data instanceof ArrayBuffer) {
    return encodeArrayBuffer(packet, supportsBinary, callback);
  } else if (typeof Blob !== 'undefined' && data instanceof Blob) {
    return encodeBlob(packet, supportsBinary, callback);
  }

  // might be an object with { base64: true, data: dataAsBase64String }
  if (data && data.base64) {
    return encodeBase64Object(packet, callback);
  }

  // Sending data as a utf-8 string
  var encoded = packets[packet.type];

  // data fragment is optional
  if (undefined !== packet.data) {
    encoded += utf8encode ? utf8.encode(String(packet.data), { strict: false }) : String(packet.data);
  }

  return callback('' + encoded);

};

function encodeBase64Object(packet, callback) {
  // packet data is an object { base64: true, data: dataAsBase64String }
  var message = 'b' + exports.packets[packet.type] + packet.data.data;
  return callback(message);
}

/**
 * Encode packet helpers for binary types
 */

function encodeArrayBuffer(packet, supportsBinary, callback) {
  if (!supportsBinary) {
    return exports.encodeBase64Packet(packet, callback);
  }

  var data = packet.data;
  var contentArray = new Uint8Array(data);
  var resultBuffer = new Uint8Array(1 + data.byteLength);

  resultBuffer[0] = packets[packet.type];
  for (var i = 0; i < contentArray.length; i++) {
    resultBuffer[i+1] = contentArray[i];
  }

  return callback(resultBuffer.buffer);
}

function encodeBlobAsArrayBuffer(packet, supportsBinary, callback) {
  if (!supportsBinary) {
    return exports.encodeBase64Packet(packet, callback);
  }

  var fr = new FileReader();
  fr.onload = function() {
    exports.encodePacket({ type: packet.type, data: fr.result }, supportsBinary, true, callback);
  };
  return fr.readAsArrayBuffer(packet.data);
}

function encodeBlob(packet, supportsBinary, callback) {
  if (!supportsBinary) {
    return exports.encodeBase64Packet(packet, callback);
  }

  if (dontSendBlobs) {
    return encodeBlobAsArrayBuffer(packet, supportsBinary, callback);
  }

  var length = new Uint8Array(1);
  length[0] = packets[packet.type];
  var blob = new Blob([length.buffer, packet.data]);

  return callback(blob);
}

/**
 * Encodes a packet with binary data in a base64 string
 *
 * @param {Object} packet, has `type` and `data`
 * @return {String} base64 encoded message
 */

exports.encodeBase64Packet = function(packet, callback) {
  var message = 'b' + exports.packets[packet.type];
  if (typeof Blob !== 'undefined' && packet.data instanceof Blob) {
    var fr = new FileReader();
    fr.onload = function() {
      var b64 = fr.result.split(',')[1];
      callback(message + b64);
    };
    return fr.readAsDataURL(packet.data);
  }

  var b64data;
  try {
    b64data = String.fromCharCode.apply(null, new Uint8Array(packet.data));
  } catch (e) {
    // iPhone Safari doesn't let you apply with typed arrays
    var typed = new Uint8Array(packet.data);
    var basic = new Array(typed.length);
    for (var i = 0; i < typed.length; i++) {
      basic[i] = typed[i];
    }
    b64data = String.fromCharCode.apply(null, basic);
  }
  message += btoa(b64data);
  return callback(message);
};

/**
 * Decodes a packet. Changes format to Blob if requested.
 *
 * @return {Object} with `type` and `data` (if any)
 * @api private
 */

exports.decodePacket = function (data, binaryType, utf8decode) {
  if (data === undefined) {
    return err;
  }
  // String data
  if (typeof data === 'string') {
    if (data.charAt(0) === 'b') {
      return exports.decodeBase64Packet(data.substr(1), binaryType);
    }

    if (utf8decode) {
      data = tryDecode(data);
      if (data === false) {
        return err;
      }
    }
    var type = data.charAt(0);

    if (Number(type) != type || !packetslist[type]) {
      return err;
    }

    if (data.length > 1) {
      return { type: packetslist[type], data: data.substring(1) };
    } else {
      return { type: packetslist[type] };
    }
  }

  var asArray = new Uint8Array(data);
  var type = asArray[0];
  var rest = sliceBuffer(data, 1);
  if (Blob && binaryType === 'blob') {
    rest = new Blob([rest]);
  }
  return { type: packetslist[type], data: rest };
};

function tryDecode(data) {
  try {
    data = utf8.decode(data, { strict: false });
  } catch (e) {
    return false;
  }
  return data;
}

/**
 * Decodes a packet encoded in a base64 string
 *
 * @param {String} base64 encoded message
 * @return {Object} with `type` and `data` (if any)
 */

exports.decodeBase64Packet = function(msg, binaryType) {
  var type = packetslist[msg.charAt(0)];
  if (!base64encoder) {
    return { type: type, data: { base64: true, data: msg.substr(1) } };
  }

  var data = base64encoder.decode(msg.substr(1));

  if (binaryType === 'blob' && Blob) {
    data = new Blob([data]);
  }

  return { type: type, data: data };
};

/**
 * Encodes multiple messages (payload).
 *
 *     <length>:data
 *
 * Example:
 *
 *     11:hello world2:hi
 *
 * If any contents are binary, they will be encoded as base64 strings. Base64
 * encoded strings are marked with a b before the length specifier
 *
 * @param {Array} packets
 * @api private
 */

exports.encodePayload = function (packets, supportsBinary, callback) {
  if (typeof supportsBinary === 'function') {
    callback = supportsBinary;
    supportsBinary = null;
  }

  var isBinary = hasBinary(packets);

  if (supportsBinary && isBinary) {
    if (Blob && !dontSendBlobs) {
      return exports.encodePayloadAsBlob(packets, callback);
    }

    return exports.encodePayloadAsArrayBuffer(packets, callback);
  }

  if (!packets.length) {
    return callback('0:');
  }

  function setLengthHeader(message) {
    return message.length + ':' + message;
  }

  function encodeOne(packet, doneCallback) {
    exports.encodePacket(packet, !isBinary ? false : supportsBinary, false, function(message) {
      doneCallback(null, setLengthHeader(message));
    });
  }

  map(packets, encodeOne, function(err, results) {
    return callback(results.join(''));
  });
};

/**
 * Async array map using after
 */

function map(ary, each, done) {
  var result = new Array(ary.length);
  var next = after(ary.length, done);

  var eachWithIndex = function(i, el, cb) {
    each(el, function(error, msg) {
      result[i] = msg;
      cb(error, result);
    });
  };

  for (var i = 0; i < ary.length; i++) {
    eachWithIndex(i, ary[i], next);
  }
}

/*
 * Decodes data when a payload is maybe expected. Possible binary contents are
 * decoded from their base64 representation
 *
 * @param {String} data, callback method
 * @api public
 */

exports.decodePayload = function (data, binaryType, callback) {
  if (typeof data !== 'string') {
    return exports.decodePayloadAsBinary(data, binaryType, callback);
  }

  if (typeof binaryType === 'function') {
    callback = binaryType;
    binaryType = null;
  }

  var packet;
  if (data === '') {
    // parser error - ignoring payload
    return callback(err, 0, 1);
  }

  var length = '', n, msg;

  for (var i = 0, l = data.length; i < l; i++) {
    var chr = data.charAt(i);

    if (chr !== ':') {
      length += chr;
      continue;
    }

    if (length === '' || (length != (n = Number(length)))) {
      // parser error - ignoring payload
      return callback(err, 0, 1);
    }

    msg = data.substr(i + 1, n);

    if (length != msg.length) {
      // parser error - ignoring payload
      return callback(err, 0, 1);
    }

    if (msg.length) {
      packet = exports.decodePacket(msg, binaryType, false);

      if (err.type === packet.type && err.data === packet.data) {
        // parser error in individual packet - ignoring payload
        return callback(err, 0, 1);
      }

      var ret = callback(packet, i + n, l);
      if (false === ret) return;
    }

    // advance cursor
    i += n;
    length = '';
  }

  if (length !== '') {
    // parser error - ignoring payload
    return callback(err, 0, 1);
  }

};

/**
 * Encodes multiple messages (payload) as binary.
 *
 * <1 = binary, 0 = string><number from 0-9><number from 0-9>[...]<number
 * 255><data>
 *
 * Example:
 * 1 3 255 1 2 3, if the binary contents are interpreted as 8 bit integers
 *
 * @param {Array} packets
 * @return {ArrayBuffer} encoded payload
 * @api private
 */

exports.encodePayloadAsArrayBuffer = function(packets, callback) {
  if (!packets.length) {
    return callback(new ArrayBuffer(0));
  }

  function encodeOne(packet, doneCallback) {
    exports.encodePacket(packet, true, true, function(data) {
      return doneCallback(null, data);
    });
  }

  map(packets, encodeOne, function(err, encodedPackets) {
    var totalLength = encodedPackets.reduce(function(acc, p) {
      var len;
      if (typeof p === 'string'){
        len = p.length;
      } else {
        len = p.byteLength;
      }
      return acc + len.toString().length + len + 2; // string/binary identifier + separator = 2
    }, 0);

    var resultArray = new Uint8Array(totalLength);

    var bufferIndex = 0;
    encodedPackets.forEach(function(p) {
      var isString = typeof p === 'string';
      var ab = p;
      if (isString) {
        var view = new Uint8Array(p.length);
        for (var i = 0; i < p.length; i++) {
          view[i] = p.charCodeAt(i);
        }
        ab = view.buffer;
      }

      if (isString) { // not true binary
        resultArray[bufferIndex++] = 0;
      } else { // true binary
        resultArray[bufferIndex++] = 1;
      }

      var lenStr = ab.byteLength.toString();
      for (var i = 0; i < lenStr.length; i++) {
        resultArray[bufferIndex++] = parseInt(lenStr[i]);
      }
      resultArray[bufferIndex++] = 255;

      var view = new Uint8Array(ab);
      for (var i = 0; i < view.length; i++) {
        resultArray[bufferIndex++] = view[i];
      }
    });

    return callback(resultArray.buffer);
  });
};

/**
 * Encode as Blob
 */

exports.encodePayloadAsBlob = function(packets, callback) {
  function encodeOne(packet, doneCallback) {
    exports.encodePacket(packet, true, true, function(encoded) {
      var binaryIdentifier = new Uint8Array(1);
      binaryIdentifier[0] = 1;
      if (typeof encoded === 'string') {
        var view = new Uint8Array(encoded.length);
        for (var i = 0; i < encoded.length; i++) {
          view[i] = encoded.charCodeAt(i);
        }
        encoded = view.buffer;
        binaryIdentifier[0] = 0;
      }

      var len = (encoded instanceof ArrayBuffer)
        ? encoded.byteLength
        : encoded.size;

      var lenStr = len.toString();
      var lengthAry = new Uint8Array(lenStr.length + 1);
      for (var i = 0; i < lenStr.length; i++) {
        lengthAry[i] = parseInt(lenStr[i]);
      }
      lengthAry[lenStr.length] = 255;

      if (Blob) {
        var blob = new Blob([binaryIdentifier.buffer, lengthAry.buffer, encoded]);
        doneCallback(null, blob);
      }
    });
  }

  map(packets, encodeOne, function(err, results) {
    return callback(new Blob(results));
  });
};

/*
 * Decodes data when a payload is maybe expected. Strings are decoded by
 * interpreting each byte as a key code for entries marked to start with 0. See
 * description of encodePayloadAsBinary
 *
 * @param {ArrayBuffer} data, callback method
 * @api public
 */

exports.decodePayloadAsBinary = function (data, binaryType, callback) {
  if (typeof binaryType === 'function') {
    callback = binaryType;
    binaryType = null;
  }

  var bufferTail = data;
  var buffers = [];

  while (bufferTail.byteLength > 0) {
    var tailArray = new Uint8Array(bufferTail);
    var isString = tailArray[0] === 0;
    var msgLength = '';

    for (var i = 1; ; i++) {
      if (tailArray[i] === 255) break;

      // 310 = char length of Number.MAX_VALUE
      if (msgLength.length > 310) {
        return callback(err, 0, 1);
      }

      msgLength += tailArray[i];
    }

    bufferTail = sliceBuffer(bufferTail, 2 + msgLength.length);
    msgLength = parseInt(msgLength);

    var msg = sliceBuffer(bufferTail, 0, msgLength);
    if (isString) {
      try {
        msg = String.fromCharCode.apply(null, new Uint8Array(msg));
      } catch (e) {
        // iPhone Safari doesn't let you apply to typed arrays
        var typed = new Uint8Array(msg);
        msg = '';
        for (var i = 0; i < typed.length; i++) {
          msg += String.fromCharCode(typed[i]);
        }
      }
    }

    buffers.push(msg);
    bufferTail = sliceBuffer(bufferTail, msgLength);
  }

  var total = buffers.length;
  buffers.forEach(function(buffer, i) {
    callback(exports.decodePacket(buffer, binaryType, true), i, total);
  });
};


/***/ }),

/***/ "./node_modules/engine.io-parser/lib/keys.js":
/*!***************************************************!*\
  !*** ./node_modules/engine.io-parser/lib/keys.js ***!
  \***************************************************/
/*! no static exports found */
/***/ (function(module, exports) {


/**
 * Gets the keys for an object.
 *
 * @return {Array} keys
 * @api private
 */

module.exports = Object.keys || function keys (obj){
  var arr = [];
  var has = Object.prototype.hasOwnProperty;

  for (var i in obj) {
    if (has.call(obj, i)) {
      arr.push(i);
    }
  }
  return arr;
};


/***/ }),

/***/ "./node_modules/engine.io-parser/lib/utf8.js":
/*!***************************************************!*\
  !*** ./node_modules/engine.io-parser/lib/utf8.js ***!
  \***************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

/*! https://mths.be/utf8js v2.1.2 by @mathias */

var stringFromCharCode = String.fromCharCode;

// Taken from https://mths.be/punycode
function ucs2decode(string) {
	var output = [];
	var counter = 0;
	var length = string.length;
	var value;
	var extra;
	while (counter < length) {
		value = string.charCodeAt(counter++);
		if (value >= 0xD800 && value <= 0xDBFF && counter < length) {
			// high surrogate, and there is a next character
			extra = string.charCodeAt(counter++);
			if ((extra & 0xFC00) == 0xDC00) { // low surrogate
				output.push(((value & 0x3FF) << 10) + (extra & 0x3FF) + 0x10000);
			} else {
				// unmatched surrogate; only append this code unit, in case the next
				// code unit is the high surrogate of a surrogate pair
				output.push(value);
				counter--;
			}
		} else {
			output.push(value);
		}
	}
	return output;
}

// Taken from https://mths.be/punycode
function ucs2encode(array) {
	var length = array.length;
	var index = -1;
	var value;
	var output = '';
	while (++index < length) {
		value = array[index];
		if (value > 0xFFFF) {
			value -= 0x10000;
			output += stringFromCharCode(value >>> 10 & 0x3FF | 0xD800);
			value = 0xDC00 | value & 0x3FF;
		}
		output += stringFromCharCode(value);
	}
	return output;
}

function checkScalarValue(codePoint, strict) {
	if (codePoint >= 0xD800 && codePoint <= 0xDFFF) {
		if (strict) {
			throw Error(
				'Lone surrogate U+' + codePoint.toString(16).toUpperCase() +
				' is not a scalar value'
			);
		}
		return false;
	}
	return true;
}
/*--------------------------------------------------------------------------*/

function createByte(codePoint, shift) {
	return stringFromCharCode(((codePoint >> shift) & 0x3F) | 0x80);
}

function encodeCodePoint(codePoint, strict) {
	if ((codePoint & 0xFFFFFF80) == 0) { // 1-byte sequence
		return stringFromCharCode(codePoint);
	}
	var symbol = '';
	if ((codePoint & 0xFFFFF800) == 0) { // 2-byte sequence
		symbol = stringFromCharCode(((codePoint >> 6) & 0x1F) | 0xC0);
	}
	else if ((codePoint & 0xFFFF0000) == 0) { // 3-byte sequence
		if (!checkScalarValue(codePoint, strict)) {
			codePoint = 0xFFFD;
		}
		symbol = stringFromCharCode(((codePoint >> 12) & 0x0F) | 0xE0);
		symbol += createByte(codePoint, 6);
	}
	else if ((codePoint & 0xFFE00000) == 0) { // 4-byte sequence
		symbol = stringFromCharCode(((codePoint >> 18) & 0x07) | 0xF0);
		symbol += createByte(codePoint, 12);
		symbol += createByte(codePoint, 6);
	}
	symbol += stringFromCharCode((codePoint & 0x3F) | 0x80);
	return symbol;
}

function utf8encode(string, opts) {
	opts = opts || {};
	var strict = false !== opts.strict;

	var codePoints = ucs2decode(string);
	var length = codePoints.length;
	var index = -1;
	var codePoint;
	var byteString = '';
	while (++index < length) {
		codePoint = codePoints[index];
		byteString += encodeCodePoint(codePoint, strict);
	}
	return byteString;
}

/*--------------------------------------------------------------------------*/

function readContinuationByte() {
	if (byteIndex >= byteCount) {
		throw Error('Invalid byte index');
	}

	var continuationByte = byteArray[byteIndex] & 0xFF;
	byteIndex++;

	if ((continuationByte & 0xC0) == 0x80) {
		return continuationByte & 0x3F;
	}

	// If we end up here, it’s not a continuation byte
	throw Error('Invalid continuation byte');
}

function decodeSymbol(strict) {
	var byte1;
	var byte2;
	var byte3;
	var byte4;
	var codePoint;

	if (byteIndex > byteCount) {
		throw Error('Invalid byte index');
	}

	if (byteIndex == byteCount) {
		return false;
	}

	// Read first byte
	byte1 = byteArray[byteIndex] & 0xFF;
	byteIndex++;

	// 1-byte sequence (no continuation bytes)
	if ((byte1 & 0x80) == 0) {
		return byte1;
	}

	// 2-byte sequence
	if ((byte1 & 0xE0) == 0xC0) {
		byte2 = readContinuationByte();
		codePoint = ((byte1 & 0x1F) << 6) | byte2;
		if (codePoint >= 0x80) {
			return codePoint;
		} else {
			throw Error('Invalid continuation byte');
		}
	}

	// 3-byte sequence (may include unpaired surrogates)
	if ((byte1 & 0xF0) == 0xE0) {
		byte2 = readContinuationByte();
		byte3 = readContinuationByte();
		codePoint = ((byte1 & 0x0F) << 12) | (byte2 << 6) | byte3;
		if (codePoint >= 0x0800) {
			return checkScalarValue(codePoint, strict) ? codePoint : 0xFFFD;
		} else {
			throw Error('Invalid continuation byte');
		}
	}

	// 4-byte sequence
	if ((byte1 & 0xF8) == 0xF0) {
		byte2 = readContinuationByte();
		byte3 = readContinuationByte();
		byte4 = readContinuationByte();
		codePoint = ((byte1 & 0x07) << 0x12) | (byte2 << 0x0C) |
			(byte3 << 0x06) | byte4;
		if (codePoint >= 0x010000 && codePoint <= 0x10FFFF) {
			return codePoint;
		}
	}

	throw Error('Invalid UTF-8 detected');
}

var byteArray;
var byteCount;
var byteIndex;
function utf8decode(byteString, opts) {
	opts = opts || {};
	var strict = false !== opts.strict;

	byteArray = ucs2decode(byteString);
	byteCount = byteArray.length;
	byteIndex = 0;
	var codePoints = [];
	var tmp;
	while ((tmp = decodeSymbol(strict)) !== false) {
		codePoints.push(tmp);
	}
	return ucs2encode(codePoints);
}

module.exports = {
	version: '2.1.2',
	encode: utf8encode,
	decode: utf8decode
};


/***/ }),

/***/ "./node_modules/events/events.js":
/*!***************************************!*\
  !*** ./node_modules/events/events.js ***!
  \***************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
// Copyright Joyent, Inc. and other Node contributors.
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the
// following conditions:
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
// NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
// USE OR OTHER DEALINGS IN THE SOFTWARE.



var R = typeof Reflect === 'object' ? Reflect : null
var ReflectApply = R && typeof R.apply === 'function'
  ? R.apply
  : function ReflectApply(target, receiver, args) {
    return Function.prototype.apply.call(target, receiver, args);
  }

var ReflectOwnKeys
if (R && typeof R.ownKeys === 'function') {
  ReflectOwnKeys = R.ownKeys
} else if (Object.getOwnPropertySymbols) {
  ReflectOwnKeys = function ReflectOwnKeys(target) {
    return Object.getOwnPropertyNames(target)
      .concat(Object.getOwnPropertySymbols(target));
  };
} else {
  ReflectOwnKeys = function ReflectOwnKeys(target) {
    return Object.getOwnPropertyNames(target);
  };
}

function ProcessEmitWarning(warning) {
  if (console && console.warn) console.warn(warning);
}

var NumberIsNaN = Number.isNaN || function NumberIsNaN(value) {
  return value !== value;
}

function EventEmitter() {
  EventEmitter.init.call(this);
}
module.exports = EventEmitter;

// Backwards-compat with node 0.10.x
EventEmitter.EventEmitter = EventEmitter;

EventEmitter.prototype._events = undefined;
EventEmitter.prototype._eventsCount = 0;
EventEmitter.prototype._maxListeners = undefined;

// By default EventEmitters will print a warning if more than 10 listeners are
// added to it. This is a useful default which helps finding memory leaks.
var defaultMaxListeners = 10;

Object.defineProperty(EventEmitter, 'defaultMaxListeners', {
  enumerable: true,
  get: function() {
    return defaultMaxListeners;
  },
  set: function(arg) {
    if (typeof arg !== 'number' || arg < 0 || NumberIsNaN(arg)) {
      throw new RangeError('The value of "defaultMaxListeners" is out of range. It must be a non-negative number. Received ' + arg + '.');
    }
    defaultMaxListeners = arg;
  }
});

EventEmitter.init = function() {

  if (this._events === undefined ||
      this._events === Object.getPrototypeOf(this)._events) {
    this._events = Object.create(null);
    this._eventsCount = 0;
  }

  this._maxListeners = this._maxListeners || undefined;
};

// Obviously not all Emitters should be limited to 10. This function allows
// that to be increased. Set to zero for unlimited.
EventEmitter.prototype.setMaxListeners = function setMaxListeners(n) {
  if (typeof n !== 'number' || n < 0 || NumberIsNaN(n)) {
    throw new RangeError('The value of "n" is out of range. It must be a non-negative number. Received ' + n + '.');
  }
  this._maxListeners = n;
  return this;
};

function $getMaxListeners(that) {
  if (that._maxListeners === undefined)
    return EventEmitter.defaultMaxListeners;
  return that._maxListeners;
}

EventEmitter.prototype.getMaxListeners = function getMaxListeners() {
  return $getMaxListeners(this);
};

EventEmitter.prototype.emit = function emit(type) {
  var args = [];
  for (var i = 1; i < arguments.length; i++) args.push(arguments[i]);
  var doError = (type === 'error');

  var events = this._events;
  if (events !== undefined)
    doError = (doError && events.error === undefined);
  else if (!doError)
    return false;

  // If there is no 'error' event listener then throw.
  if (doError) {
    var er;
    if (args.length > 0)
      er = args[0];
    if (er instanceof Error) {
      // Note: The comments on the `throw` lines are intentional, they show
      // up in Node's output if this results in an unhandled exception.
      throw er; // Unhandled 'error' event
    }
    // At least give some kind of context to the user
    var err = new Error('Unhandled error.' + (er ? ' (' + er.message + ')' : ''));
    err.context = er;
    throw err; // Unhandled 'error' event
  }

  var handler = events[type];

  if (handler === undefined)
    return false;

  if (typeof handler === 'function') {
    ReflectApply(handler, this, args);
  } else {
    var len = handler.length;
    var listeners = arrayClone(handler, len);
    for (var i = 0; i < len; ++i)
      ReflectApply(listeners[i], this, args);
  }

  return true;
};

function _addListener(target, type, listener, prepend) {
  var m;
  var events;
  var existing;

  if (typeof listener !== 'function') {
    throw new TypeError('The "listener" argument must be of type Function. Received type ' + typeof listener);
  }

  events = target._events;
  if (events === undefined) {
    events = target._events = Object.create(null);
    target._eventsCount = 0;
  } else {
    // To avoid recursion in the case that type === "newListener"! Before
    // adding it to the listeners, first emit "newListener".
    if (events.newListener !== undefined) {
      target.emit('newListener', type,
                  listener.listener ? listener.listener : listener);

      // Re-assign `events` because a newListener handler could have caused the
      // this._events to be assigned to a new object
      events = target._events;
    }
    existing = events[type];
  }

  if (existing === undefined) {
    // Optimize the case of one listener. Don't need the extra array object.
    existing = events[type] = listener;
    ++target._eventsCount;
  } else {
    if (typeof existing === 'function') {
      // Adding the second element, need to change to array.
      existing = events[type] =
        prepend ? [listener, existing] : [existing, listener];
      // If we've already got an array, just append.
    } else if (prepend) {
      existing.unshift(listener);
    } else {
      existing.push(listener);
    }

    // Check for listener leak
    m = $getMaxListeners(target);
    if (m > 0 && existing.length > m && !existing.warned) {
      existing.warned = true;
      // No error code for this since it is a Warning
      // eslint-disable-next-line no-restricted-syntax
      var w = new Error('Possible EventEmitter memory leak detected. ' +
                          existing.length + ' ' + String(type) + ' listeners ' +
                          'added. Use emitter.setMaxListeners() to ' +
                          'increase limit');
      w.name = 'MaxListenersExceededWarning';
      w.emitter = target;
      w.type = type;
      w.count = existing.length;
      ProcessEmitWarning(w);
    }
  }

  return target;
}

EventEmitter.prototype.addListener = function addListener(type, listener) {
  return _addListener(this, type, listener, false);
};

EventEmitter.prototype.on = EventEmitter.prototype.addListener;

EventEmitter.prototype.prependListener =
    function prependListener(type, listener) {
      return _addListener(this, type, listener, true);
    };

function onceWrapper() {
  var args = [];
  for (var i = 0; i < arguments.length; i++) args.push(arguments[i]);
  if (!this.fired) {
    this.target.removeListener(this.type, this.wrapFn);
    this.fired = true;
    ReflectApply(this.listener, this.target, args);
  }
}

function _onceWrap(target, type, listener) {
  var state = { fired: false, wrapFn: undefined, target: target, type: type, listener: listener };
  var wrapped = onceWrapper.bind(state);
  wrapped.listener = listener;
  state.wrapFn = wrapped;
  return wrapped;
}

EventEmitter.prototype.once = function once(type, listener) {
  if (typeof listener !== 'function') {
    throw new TypeError('The "listener" argument must be of type Function. Received type ' + typeof listener);
  }
  this.on(type, _onceWrap(this, type, listener));
  return this;
};

EventEmitter.prototype.prependOnceListener =
    function prependOnceListener(type, listener) {
      if (typeof listener !== 'function') {
        throw new TypeError('The "listener" argument must be of type Function. Received type ' + typeof listener);
      }
      this.prependListener(type, _onceWrap(this, type, listener));
      return this;
    };

// Emits a 'removeListener' event if and only if the listener was removed.
EventEmitter.prototype.removeListener =
    function removeListener(type, listener) {
      var list, events, position, i, originalListener;

      if (typeof listener !== 'function') {
        throw new TypeError('The "listener" argument must be of type Function. Received type ' + typeof listener);
      }

      events = this._events;
      if (events === undefined)
        return this;

      list = events[type];
      if (list === undefined)
        return this;

      if (list === listener || list.listener === listener) {
        if (--this._eventsCount === 0)
          this._events = Object.create(null);
        else {
          delete events[type];
          if (events.removeListener)
            this.emit('removeListener', type, list.listener || listener);
        }
      } else if (typeof list !== 'function') {
        position = -1;

        for (i = list.length - 1; i >= 0; i--) {
          if (list[i] === listener || list[i].listener === listener) {
            originalListener = list[i].listener;
            position = i;
            break;
          }
        }

        if (position < 0)
          return this;

        if (position === 0)
          list.shift();
        else {
          spliceOne(list, position);
        }

        if (list.length === 1)
          events[type] = list[0];

        if (events.removeListener !== undefined)
          this.emit('removeListener', type, originalListener || listener);
      }

      return this;
    };

EventEmitter.prototype.off = EventEmitter.prototype.removeListener;

EventEmitter.prototype.removeAllListeners =
    function removeAllListeners(type) {
      var listeners, events, i;

      events = this._events;
      if (events === undefined)
        return this;

      // not listening for removeListener, no need to emit
      if (events.removeListener === undefined) {
        if (arguments.length === 0) {
          this._events = Object.create(null);
          this._eventsCount = 0;
        } else if (events[type] !== undefined) {
          if (--this._eventsCount === 0)
            this._events = Object.create(null);
          else
            delete events[type];
        }
        return this;
      }

      // emit removeListener for all listeners on all events
      if (arguments.length === 0) {
        var keys = Object.keys(events);
        var key;
        for (i = 0; i < keys.length; ++i) {
          key = keys[i];
          if (key === 'removeListener') continue;
          this.removeAllListeners(key);
        }
        this.removeAllListeners('removeListener');
        this._events = Object.create(null);
        this._eventsCount = 0;
        return this;
      }

      listeners = events[type];

      if (typeof listeners === 'function') {
        this.removeListener(type, listeners);
      } else if (listeners !== undefined) {
        // LIFO order
        for (i = listeners.length - 1; i >= 0; i--) {
          this.removeListener(type, listeners[i]);
        }
      }

      return this;
    };

function _listeners(target, type, unwrap) {
  var events = target._events;

  if (events === undefined)
    return [];

  var evlistener = events[type];
  if (evlistener === undefined)
    return [];

  if (typeof evlistener === 'function')
    return unwrap ? [evlistener.listener || evlistener] : [evlistener];

  return unwrap ?
    unwrapListeners(evlistener) : arrayClone(evlistener, evlistener.length);
}

EventEmitter.prototype.listeners = function listeners(type) {
  return _listeners(this, type, true);
};

EventEmitter.prototype.rawListeners = function rawListeners(type) {
  return _listeners(this, type, false);
};

EventEmitter.listenerCount = function(emitter, type) {
  if (typeof emitter.listenerCount === 'function') {
    return emitter.listenerCount(type);
  } else {
    return listenerCount.call(emitter, type);
  }
};

EventEmitter.prototype.listenerCount = listenerCount;
function listenerCount(type) {
  var events = this._events;

  if (events !== undefined) {
    var evlistener = events[type];

    if (typeof evlistener === 'function') {
      return 1;
    } else if (evlistener !== undefined) {
      return evlistener.length;
    }
  }

  return 0;
}

EventEmitter.prototype.eventNames = function eventNames() {
  return this._eventsCount > 0 ? ReflectOwnKeys(this._events) : [];
};

function arrayClone(arr, n) {
  var copy = new Array(n);
  for (var i = 0; i < n; ++i)
    copy[i] = arr[i];
  return copy;
}

function spliceOne(list, index) {
  for (; index + 1 < list.length; index++)
    list[index] = list[index + 1];
  list.pop();
}

function unwrapListeners(arr) {
  var ret = new Array(arr.length);
  for (var i = 0; i < ret.length; ++i) {
    ret[i] = arr[i].listener || arr[i];
  }
  return ret;
}


/***/ }),

/***/ "./node_modules/has-binary2/index.js":
/*!*******************************************!*\
  !*** ./node_modules/has-binary2/index.js ***!
  \*******************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

/* WEBPACK VAR INJECTION */(function(Buffer) {/* global Blob File */

/*
 * Module requirements.
 */

var isArray = __webpack_require__(/*! isarray */ "./node_modules/isarray/index.js");

var toString = Object.prototype.toString;
var withNativeBlob = typeof Blob === 'function' ||
                        typeof Blob !== 'undefined' && toString.call(Blob) === '[object BlobConstructor]';
var withNativeFile = typeof File === 'function' ||
                        typeof File !== 'undefined' && toString.call(File) === '[object FileConstructor]';

/**
 * Module exports.
 */

module.exports = hasBinary;

/**
 * Checks for binary data.
 *
 * Supports Buffer, ArrayBuffer, Blob and File.
 *
 * @param {Object} anything
 * @api public
 */

function hasBinary (obj) {
  if (!obj || typeof obj !== 'object') {
    return false;
  }

  if (isArray(obj)) {
    for (var i = 0, l = obj.length; i < l; i++) {
      if (hasBinary(obj[i])) {
        return true;
      }
    }
    return false;
  }

  if ((typeof Buffer === 'function' && Buffer.isBuffer && Buffer.isBuffer(obj)) ||
    (typeof ArrayBuffer === 'function' && obj instanceof ArrayBuffer) ||
    (withNativeBlob && obj instanceof Blob) ||
    (withNativeFile && obj instanceof File)
  ) {
    return true;
  }

  // see: https://github.com/Automattic/has-binary/pull/4
  if (obj.toJSON && typeof obj.toJSON === 'function' && arguments.length === 1) {
    return hasBinary(obj.toJSON(), true);
  }

  for (var key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key) && hasBinary(obj[key])) {
      return true;
    }
  }

  return false;
}

/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./../buffer/index.js */ "./node_modules/buffer/index.js").Buffer))

/***/ }),

/***/ "./node_modules/has-cors/index.js":
/*!****************************************!*\
  !*** ./node_modules/has-cors/index.js ***!
  \****************************************/
/*! no static exports found */
/***/ (function(module, exports) {


/**
 * Module exports.
 *
 * Logic borrowed from Modernizr:
 *
 *   - https://github.com/Modernizr/Modernizr/blob/master/feature-detects/cors.js
 */

try {
  module.exports = typeof XMLHttpRequest !== 'undefined' &&
    'withCredentials' in new XMLHttpRequest();
} catch (err) {
  // if XMLHttp support is disabled in IE then it will throw
  // when trying to create
  module.exports = false;
}


/***/ }),

/***/ "./node_modules/ieee754/index.js":
/*!***************************************!*\
  !*** ./node_modules/ieee754/index.js ***!
  \***************************************/
/*! no static exports found */
/***/ (function(module, exports) {

exports.read = function (buffer, offset, isLE, mLen, nBytes) {
  var e, m
  var eLen = (nBytes * 8) - mLen - 1
  var eMax = (1 << eLen) - 1
  var eBias = eMax >> 1
  var nBits = -7
  var i = isLE ? (nBytes - 1) : 0
  var d = isLE ? -1 : 1
  var s = buffer[offset + i]

  i += d

  e = s & ((1 << (-nBits)) - 1)
  s >>= (-nBits)
  nBits += eLen
  for (; nBits > 0; e = (e * 256) + buffer[offset + i], i += d, nBits -= 8) {}

  m = e & ((1 << (-nBits)) - 1)
  e >>= (-nBits)
  nBits += mLen
  for (; nBits > 0; m = (m * 256) + buffer[offset + i], i += d, nBits -= 8) {}

  if (e === 0) {
    e = 1 - eBias
  } else if (e === eMax) {
    return m ? NaN : ((s ? -1 : 1) * Infinity)
  } else {
    m = m + Math.pow(2, mLen)
    e = e - eBias
  }
  return (s ? -1 : 1) * m * Math.pow(2, e - mLen)
}

exports.write = function (buffer, value, offset, isLE, mLen, nBytes) {
  var e, m, c
  var eLen = (nBytes * 8) - mLen - 1
  var eMax = (1 << eLen) - 1
  var eBias = eMax >> 1
  var rt = (mLen === 23 ? Math.pow(2, -24) - Math.pow(2, -77) : 0)
  var i = isLE ? 0 : (nBytes - 1)
  var d = isLE ? 1 : -1
  var s = value < 0 || (value === 0 && 1 / value < 0) ? 1 : 0

  value = Math.abs(value)

  if (isNaN(value) || value === Infinity) {
    m = isNaN(value) ? 1 : 0
    e = eMax
  } else {
    e = Math.floor(Math.log(value) / Math.LN2)
    if (value * (c = Math.pow(2, -e)) < 1) {
      e--
      c *= 2
    }
    if (e + eBias >= 1) {
      value += rt / c
    } else {
      value += rt * Math.pow(2, 1 - eBias)
    }
    if (value * c >= 2) {
      e++
      c /= 2
    }

    if (e + eBias >= eMax) {
      m = 0
      e = eMax
    } else if (e + eBias >= 1) {
      m = ((value * c) - 1) * Math.pow(2, mLen)
      e = e + eBias
    } else {
      m = value * Math.pow(2, eBias - 1) * Math.pow(2, mLen)
      e = 0
    }
  }

  for (; mLen >= 8; buffer[offset + i] = m & 0xff, i += d, m /= 256, mLen -= 8) {}

  e = (e << mLen) | m
  eLen += mLen
  for (; eLen > 0; buffer[offset + i] = e & 0xff, i += d, e /= 256, eLen -= 8) {}

  buffer[offset + i - d] |= s * 128
}


/***/ }),

/***/ "./node_modules/indexof/index.js":
/*!***************************************!*\
  !*** ./node_modules/indexof/index.js ***!
  \***************************************/
/*! no static exports found */
/***/ (function(module, exports) {


var indexOf = [].indexOf;

module.exports = function(arr, obj){
  if (indexOf) return arr.indexOf(obj);
  for (var i = 0; i < arr.length; ++i) {
    if (arr[i] === obj) return i;
  }
  return -1;
};

/***/ }),

/***/ "./node_modules/isarray/index.js":
/*!***************************************!*\
  !*** ./node_modules/isarray/index.js ***!
  \***************************************/
/*! no static exports found */
/***/ (function(module, exports) {

var toString = {}.toString;

module.exports = Array.isArray || function (arr) {
  return toString.call(arr) == '[object Array]';
};


/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/CommandQueue.js":
/*!***************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/CommandQueue.js ***!
  \***************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _events = __webpack_require__(/*! events */ "./node_modules/events/events.js");

var _Logger = _interopRequireDefault(__webpack_require__(/*! ./Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _errors = __webpack_require__(/*! ./errors */ "./node_modules/mediasoup-client/lib-es5/errors.js");

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var logger = new _Logger.default('CommandQueue');

var CommandQueue =
/*#__PURE__*/
function (_EventEmitter) {
  _inherits(CommandQueue, _EventEmitter);

  function CommandQueue() {
    var _this;

    _classCallCheck(this, CommandQueue);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(CommandQueue).call(this));

    _this.setMaxListeners(Infinity); // Closed flag.
    // @type {Boolean}


    _this._closed = false; // Busy running a command.
    // @type {Boolean}

    _this._busy = false; // Queue for pending commands. Each command is an Object with method,
    // resolve, reject, and other members (depending the case).
    // @type {Array<Object>}

    _this._queue = [];
    return _this;
  }

  _createClass(CommandQueue, [{
    key: "close",
    value: function close() {
      this._closed = true;
    }
  }, {
    key: "push",
    value: function push(method, data) {
      var _this2 = this;

      var command = Object.assign({
        method: method
      }, data);
      logger.debug('push() [method:%s]', method);
      return new Promise(function (resolve, reject) {
        var queue = _this2._queue;
        command.resolve = resolve;
        command.reject = reject; // Append command to the queue.

        queue.push(command);

        _this2._handlePendingCommands();
      });
    }
  }, {
    key: "_handlePendingCommands",
    value: function _handlePendingCommands() {
      var _this3 = this;

      if (this._busy) return;
      var queue = this._queue; // Take the first command.

      var command = queue[0];
      if (!command) return;
      this._busy = true; // Execute it.

      this._handleCommand(command).then(function () {
        _this3._busy = false; // Remove the first command (the completed one) from the queue.

        queue.shift(); // And continue.

        _this3._handlePendingCommands();
      });
    }
  }, {
    key: "_handleCommand",
    value: function _handleCommand(command) {
      var _this4 = this;

      logger.debug('_handleCommand() [method:%s]', command.method);

      if (this._closed) {
        command.reject(new _errors.InvalidStateError('closed'));
        return Promise.resolve();
      }

      var promiseHolder = {
        promise: null
      };
      this.emit('exec', command, promiseHolder);
      return Promise.resolve().then(function () {
        return promiseHolder.promise;
      }).then(function (result) {
        logger.debug('_handleCommand() | command succeeded [method:%s]', command.method);

        if (_this4._closed) {
          command.reject(new _errors.InvalidStateError('closed'));
          return;
        } // Resolve the command with the given result (if any).


        command.resolve(result);
      }).catch(function (error) {
        logger.error('_handleCommand() | command failed [method:%s]: %o', command.method, error); // Reject the command with the error.

        command.reject(error);
      });
    }
  }]);

  return CommandQueue;
}(_events.EventEmitter);

exports.default = CommandQueue;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/Consumer.js":
/*!***********************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/Consumer.js ***!
  \***********************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _Logger = _interopRequireDefault(__webpack_require__(/*! ./Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter2 = _interopRequireDefault(__webpack_require__(/*! ./EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

var _errors = __webpack_require__(/*! ./errors */ "./node_modules/mediasoup-client/lib-es5/errors.js");

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var PROFILES = new Set(['default', 'low', 'medium', 'high']);
var DEFAULT_STATS_INTERVAL = 1000;
var logger = new _Logger.default('Consumer');

var Consumer =
/*#__PURE__*/
function (_EnhancedEventEmitter) {
  _inherits(Consumer, _EnhancedEventEmitter);

  /**
   * @private
   *
   * @emits {originator: String, [appData]: Any} pause
   * @emits {originator: String, [appData]: Any} resume
   * @emits {profile: String} effectiveprofilechange
   * @emits {stats: Object} stats
   * @emits handled
   * @emits unhandled
   * @emits {originator: String} close
   *
   * @emits @close
   */
  function Consumer(id, kind, rtpParameters, peer, appData) {
    var _this;

    _classCallCheck(this, Consumer);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(Consumer).call(this, logger)); // Id.
    // @type {Number}

    _this._id = id; // Closed flag.
    // @type {Boolean}

    _this._closed = false; // Media kind.
    // @type {String}

    _this._kind = kind; // RTP parameters.
    // @type {RTCRtpParameters}

    _this._rtpParameters = rtpParameters; // Associated Peer.
    // @type {Peer}

    _this._peer = peer; // App custom data.
    // @type {Any}

    _this._appData = appData; // Whether we can receive this Consumer (based on our RTP capabilities).
    // @type {Boolean}

    _this._supported = false; // Associated Transport.
    // @type {Transport}

    _this._transport = null; // Remote track.
    // @type {MediaStreamTrack}

    _this._track = null; // Locally paused flag.
    // @type {Boolean}

    _this._locallyPaused = false; // Remotely paused flag.
    // @type {Boolean}

    _this._remotelyPaused = false; // Periodic stats flag.
    // @type {Boolean}

    _this._statsEnabled = false; // Periodic stats gathering interval (milliseconds).
    // @type {Number}

    _this._statsInterval = DEFAULT_STATS_INTERVAL; // Preferred profile.
    // @type {String}

    _this._preferredProfile = 'default'; // Effective profile.
    // @type {String}

    _this._effectiveProfile = null;
    return _this;
  }
  /**
   * Consumer id.
   *
   * @return {Number}
   */


  _createClass(Consumer, [{
    key: "close",

    /**
     * Closes the Consumer.
     * This is called when the local Room is closed.
     *
     * @private
     */
    value: function close() {
      logger.debug('close()');
      if (this._closed) return;
      this._closed = true;

      if (this._statsEnabled) {
        this._statsEnabled = false;
        if (this.transport) this.transport.disableConsumerStats(this);
      }

      this.emit('@close');
      this.safeEmit('close', 'local');

      this._destroy();
    }
    /**
     * My remote Consumer was closed.
     * Invoked via remote notification.
     *
     * @private
     */

  }, {
    key: "remoteClose",
    value: function remoteClose() {
      logger.debug('remoteClose()');
      if (this._closed) return;
      this._closed = true;
      if (this._transport) this._transport.removeConsumer(this);

      this._destroy();

      this.emit('@close');
      this.safeEmit('close', 'remote');
    }
  }, {
    key: "_destroy",
    value: function _destroy() {
      this._transport = null;

      try {
        this._track.stop();
      } catch (error) {}

      this._track = null;
    }
    /**
     * Receives RTP.
     *
     * @param {transport} Transport instance.
     *
     * @return {Promise} Resolves with a remote MediaStreamTrack.
     */

  }, {
    key: "receive",
    value: function receive(transport) {
      var _this2 = this;

      logger.debug('receive() [transport:%o]', transport);
      if (this._closed) return Promise.reject(new _errors.InvalidStateError('Consumer closed'));else if (!this._supported) return Promise.reject(new Error('unsupported codecs'));else if (this._transport) return Promise.reject(new Error('already handled by a Transport'));else if (_typeof(transport) !== 'object') return Promise.reject(new TypeError('invalid Transport'));
      this._transport = transport;
      return transport.addConsumer(this).then(function (track) {
        _this2._track = track; // If we were paused, disable the track.

        if (_this2.paused) track.enabled = false;
        transport.once('@close', function () {
          if (_this2._closed || _this2._transport !== transport) return;
          _this2._transport = null;

          try {
            _this2._track.stop();
          } catch (error) {}

          _this2._track = null;

          _this2.safeEmit('unhandled');
        });

        _this2.safeEmit('handled');

        if (_this2._statsEnabled) transport.enableConsumerStats(_this2, _this2._statsInterval);
        return track;
      }).catch(function (error) {
        _this2._transport = null;
        throw error;
      });
    }
    /**
     * Pauses receiving media.
     *
     * @param {Any} [appData] - App custom data.
     *
     * @return {Boolean} true if paused.
     */

  }, {
    key: "pause",
    value: function pause(appData) {
      logger.debug('pause()');

      if (this._closed) {
        logger.error('pause() | Consumer closed');
        return false;
      } else if (this._locallyPaused) {
        return true;
      }

      this._locallyPaused = true;
      if (this._track) this._track.enabled = false;
      if (this._transport) this._transport.pauseConsumer(this, appData);
      this.safeEmit('pause', 'local', appData); // Return true if really paused.

      return this.paused;
    }
    /**
     * My remote Consumer was paused.
     * Invoked via remote notification.
     *
     * @private
     *
     * @param {Any} [appData] - App custom data.
     */

  }, {
    key: "remotePause",
    value: function remotePause(appData) {
      logger.debug('remotePause()');
      if (this._closed || this._remotelyPaused) return;
      this._remotelyPaused = true;
      if (this._track) this._track.enabled = false;
      this.safeEmit('pause', 'remote', appData);
    }
    /**
     * Resumes receiving media.
     *
     * @param {Any} [appData] - App custom data.
     *
     * @return {Boolean} true if not paused.
     */

  }, {
    key: "resume",
    value: function resume(appData) {
      logger.debug('resume()');

      if (this._closed) {
        logger.error('resume() | Consumer closed');
        return false;
      } else if (!this._locallyPaused) {
        return true;
      }

      this._locallyPaused = false;
      if (this._track && !this._remotelyPaused) this._track.enabled = true;
      if (this._transport) this._transport.resumeConsumer(this, appData);
      this.safeEmit('resume', 'local', appData); // Return true if not paused.

      return !this.paused;
    }
    /**
     * My remote Consumer was resumed.
     * Invoked via remote notification.
     *
     * @private
     *
     * @param {Any} [appData] - App custom data.
     */

  }, {
    key: "remoteResume",
    value: function remoteResume(appData) {
      logger.debug('remoteResume()');
      if (this._closed || !this._remotelyPaused) return;
      this._remotelyPaused = false;
      if (this._track && !this._locallyPaused) this._track.enabled = true;
      this.safeEmit('resume', 'remote', appData);
    }
    /**
     * Set preferred receiving profile.
     *
     * @param {String} profile
     */

  }, {
    key: "setPreferredProfile",
    value: function setPreferredProfile(profile) {
      logger.debug('setPreferredProfile() [profile:%s]', profile);

      if (this._closed) {
        logger.error('setPreferredProfile() | Consumer closed');
        return;
      } else if (profile === this._preferredProfile) {
        return;
      } else if (!PROFILES.has(profile)) {
        logger.error('setPreferredProfile() | invalid profile "%s"', profile);
        return;
      }

      this._preferredProfile = profile;
      if (this._transport) this._transport.setConsumerPreferredProfile(this, this._preferredProfile);
    }
    /**
     * Preferred receiving profile was set on my remote Consumer.
     *
     * @param {String} profile
     */

  }, {
    key: "remoteSetPreferredProfile",
    value: function remoteSetPreferredProfile(profile) {
      logger.debug('remoteSetPreferredProfile() [profile:%s]', profile);
      if (this._closed || profile === this._preferredProfile) return;
      this._preferredProfile = profile;
    }
    /**
     * Effective receiving profile changed on my remote Consumer.
     *
     * @param {String} profile
     */

  }, {
    key: "remoteEffectiveProfileChanged",
    value: function remoteEffectiveProfileChanged(profile) {
      logger.debug('remoteEffectiveProfileChanged() [profile:%s]', profile);
      if (this._closed || profile === this._effectiveProfile) return;
      this._effectiveProfile = profile;
      this.safeEmit('effectiveprofilechange', this._effectiveProfile);
    }
    /**
     * Enables periodic stats retrieval.
     */

  }, {
    key: "enableStats",
    value: function enableStats() {
      var interval = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : DEFAULT_STATS_INTERVAL;
      logger.debug('enableStats() [interval:%s]', interval);

      if (this._closed) {
        logger.error('enableStats() | Consumer closed');
        return;
      }

      if (this._statsEnabled) return;
      if (typeof interval !== 'number' || interval < 1000) this._statsInterval = DEFAULT_STATS_INTERVAL;else this._statsInterval = interval;
      this._statsEnabled = true;
      if (this._transport) this._transport.enableConsumerStats(this, this._statsInterval);
    }
    /**
     * Disables periodic stats retrieval.
     */

  }, {
    key: "disableStats",
    value: function disableStats() {
      logger.debug('disableStats()');

      if (this._closed) {
        logger.error('disableStats() | Consumer closed');
        return;
      }

      if (!this._statsEnabled) return;
      this._statsEnabled = false;
      if (this._transport) this._transport.disableConsumerStats(this);
    }
    /**
     * Mark this Consumer as suitable for reception or not.
     *
     * @private
     *
     * @param {Boolean} flag
     */

  }, {
    key: "setSupported",
    value: function setSupported(flag) {
      this._supported = flag;
    }
    /**
     * Receive remote stats.
     *
     * @private
     *
     * @param {Object} stats
     */

  }, {
    key: "remoteStats",
    value: function remoteStats(stats) {
      this.safeEmit('stats', stats);
    }
  }, {
    key: "id",
    get: function get() {
      return this._id;
    }
    /**
     * Whether the Consumer is closed.
     *
     * @return {Boolean}
     */

  }, {
    key: "closed",
    get: function get() {
      return this._closed;
    }
    /**
     * Media kind.
     *
     * @return {String}
     */

  }, {
    key: "kind",
    get: function get() {
      return this._kind;
    }
    /**
     * RTP parameters.
     *
     * @return {RTCRtpParameters}
     */

  }, {
    key: "rtpParameters",
    get: function get() {
      return this._rtpParameters;
    }
    /**
     * Associated Peer.
     *
     * @return {Peer}
     */

  }, {
    key: "peer",
    get: function get() {
      return this._peer;
    }
    /**
     * App custom data.
     *
     * @return {Any}
     */

  }, {
    key: "appData",
    get: function get() {
      return this._appData;
    }
    /**
     * Whether we can receive this Consumer (based on our RTP capabilities).
     *
     * @return {Boolean}
     */

  }, {
    key: "supported",
    get: function get() {
      return this._supported;
    }
    /**
     * Associated Transport.
     *
     * @return {Transport}
     */

  }, {
    key: "transport",
    get: function get() {
      return this._transport;
    }
    /**
     * The associated track (if any yet).
     *
     * @return {MediaStreamTrack|null}
     */

  }, {
    key: "track",
    get: function get() {
      return this._track;
    }
    /**
     * Whether the Consumer is locally paused.
     *
     * @return {Boolean}
     */

  }, {
    key: "locallyPaused",
    get: function get() {
      return this._locallyPaused;
    }
    /**
     * Whether the Consumer is remotely paused.
     *
     * @return {Boolean}
     */

  }, {
    key: "remotelyPaused",
    get: function get() {
      return this._remotelyPaused;
    }
    /**
     * Whether the Consumer is paused.
     *
     * @return {Boolean}
     */

  }, {
    key: "paused",
    get: function get() {
      return this._locallyPaused || this._remotelyPaused;
    }
    /**
     * The preferred profile.
     *
     * @type {String}
     */

  }, {
    key: "preferredProfile",
    get: function get() {
      return this._preferredProfile;
    }
    /**
     * The effective profile.
     *
     * @type {String}
     */

  }, {
    key: "effectiveProfile",
    get: function get() {
      return this._effectiveProfile;
    }
  }]);

  return Consumer;
}(_EnhancedEventEmitter2.default);

exports.default = Consumer;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/Device.js":
/*!*********************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/Device.js ***!
  \*********************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(global) {

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _bowser = _interopRequireDefault(__webpack_require__(/*! bowser */ "./node_modules/bowser/src/bowser.js"));

var _Logger = _interopRequireDefault(__webpack_require__(/*! ./Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _Chrome = _interopRequireDefault(__webpack_require__(/*! ./handlers/Chrome70 */ "./node_modules/mediasoup-client/lib-es5/handlers/Chrome70.js"));

var _Chrome2 = _interopRequireDefault(__webpack_require__(/*! ./handlers/Chrome69 */ "./node_modules/mediasoup-client/lib-es5/handlers/Chrome69.js"));

var _Chrome3 = _interopRequireDefault(__webpack_require__(/*! ./handlers/Chrome67 */ "./node_modules/mediasoup-client/lib-es5/handlers/Chrome67.js"));

var _Chrome4 = _interopRequireDefault(__webpack_require__(/*! ./handlers/Chrome55 */ "./node_modules/mediasoup-client/lib-es5/handlers/Chrome55.js"));

var _Safari = _interopRequireDefault(__webpack_require__(/*! ./handlers/Safari12 */ "./node_modules/mediasoup-client/lib-es5/handlers/Safari12.js"));

var _Safari2 = _interopRequireDefault(__webpack_require__(/*! ./handlers/Safari11 */ "./node_modules/mediasoup-client/lib-es5/handlers/Safari11.js"));

var _Firefox = _interopRequireDefault(__webpack_require__(/*! ./handlers/Firefox65 */ "./node_modules/mediasoup-client/lib-es5/handlers/Firefox65.js"));

var _Firefox2 = _interopRequireDefault(__webpack_require__(/*! ./handlers/Firefox59 */ "./node_modules/mediasoup-client/lib-es5/handlers/Firefox59.js"));

var _Firefox3 = _interopRequireDefault(__webpack_require__(/*! ./handlers/Firefox50 */ "./node_modules/mediasoup-client/lib-es5/handlers/Firefox50.js"));

var _Edge = _interopRequireDefault(__webpack_require__(/*! ./handlers/Edge11 */ "./node_modules/mediasoup-client/lib-es5/handlers/Edge11.js"));

var _ReactNative = _interopRequireDefault(__webpack_require__(/*! ./handlers/ReactNative */ "./node_modules/mediasoup-client/lib-es5/handlers/ReactNative.js"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

var logger = new _Logger.default('Device');
/**
 * Class with static members representing the underlying device or browser.
 */

var Device =
/*#__PURE__*/
function () {
  function Device() {
    _classCallCheck(this, Device);
  }

  _createClass(Device, null, [{
    key: "setHandler",

    /**
     * Provides a custom RTC handler class and avoid auto-detection. Useful
     * for making mediasoup-client work with custom devices.
     *
     * NOTE: This function must be called upon library load.
     *
     * @param {Class} handler - A handler class.
     * @param {Object} [metadata] - Handler metadata.
     * @param {String} [metadata.flag] - Handler flag.
     * @param {String} [metadata.name] - Handler name.
     * @param {String} [metadata.version] - Handler version.
     * @param {Object} [metadata.bowser] - Handler bowser Object.
     */
    value: function setHandler(handler) {
      var metadata = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
      Device._detected = true;
      Device._handlerClass = handler; // Optional fields.

      Device._flag = metadata.flag;
      Device._name = metadata.name;
      Device._version = metadata.version;
      Device._bowser = metadata.bowser || {};
    }
    /**
     * Get the device flag.
     *
     * @return {String}
     */

  }, {
    key: "getFlag",
    value: function getFlag() {
      if (!Device._detected) Device._detect();
      return Device._flag;
    }
    /**
     * Get the device name.
     *
     * @return {String}
     */

  }, {
    key: "getName",
    value: function getName() {
      if (!Device._detected) Device._detect();
      return Device._name;
    }
    /**
     * Get the device version.
     *
     * @return {String}
     */

  }, {
    key: "getVersion",
    value: function getVersion() {
      if (!Device._detected) Device._detect();
      return Device._version;
    }
    /**
     * Get the bowser module Object.
     *
     * @return {Object}
     */

  }, {
    key: "getBowser",
    value: function getBowser() {
      if (!Device._detected) Device._detect();
      return Device._bowser;
    }
    /**
     * Whether this device is supported.
     *
     * @return {Boolean}
     */

  }, {
    key: "isSupported",
    value: function isSupported() {
      if (!Device._detected) Device._detect();
      return Boolean(Device._handlerClass);
    }
    /**
     * Returns a suitable WebRTC handler class.
     *
     * @type {Class}
     */

  }, {
    key: "_detect",

    /**
     * Detects the current device/browser.
     *
     * @private
     */
    value: function _detect() {
      Device._detected = true; // If this is React-Native manually fill data.

      if (global.navigator && global.navigator.product === 'ReactNative') {
        Device._flag = 'react-native';
        Device._name = 'ReactNative';
        Device._version = undefined; // NOTE: No idea how to know it.

        Device._bowser = {};
        Device._handlerClass = _ReactNative.default;
      } // If this is a browser use bowser module detection.
      else if (global.navigator && typeof global.navigator.userAgent === 'string') {
          var ua = global.navigator.userAgent;

          var browser = _bowser.default.detect(ua);

          Device._flag = undefined;
          Device._name = browser.name || undefined;
          Device._version = browser.version || undefined;
          Device._bowser = browser;
          Device._handlerClass = null; // Chrome, Chromium (desktop and mobile).

          if (_bowser.default.check({
            chrome: '70',
            chromium: '70'
          }, true, ua)) {
            Device._flag = 'chrome';
            Device._handlerClass = _Chrome.default;
          } else if (_bowser.default.check({
            chrome: '69',
            chromium: '69'
          }, true, ua)) {
            Device._flag = 'chrome';
            Device._handlerClass = _Chrome2.default;
          } else if (_bowser.default.check({
            chrome: '67',
            chromium: '67'
          }, true, ua)) {
            Device._flag = 'chrome';
            Device._handlerClass = _Chrome3.default;
          } else if (_bowser.default.check({
            chrome: '55',
            chromium: '55'
          }, true, ua)) {
            Device._flag = 'chrome';
            Device._handlerClass = _Chrome4.default;
          } // Special case for old Chrome >= 49 if webrtc-adapter is present.
          else if (_bowser.default.check({
              chrome: '49',
              chromium: '49'
            }, true, ua) && global.adapter) {
              Device._flag = 'chrome';
              Device._handlerClass = _Chrome4.default;
            } // Firefox (desktop and mobile).
            else if (_bowser.default.check({
                firefox: '65'
              }, true, ua)) {
                Device._flag = 'firefox';
                Device._handlerClass = _Firefox.default;
              } // Firefox (desktop and mobile).
              else if (_bowser.default.check({
                  firefox: '59'
                }, true, ua)) {
                  Device._flag = 'firefox';
                  Device._handlerClass = _Firefox2.default;
                } else if (_bowser.default.check({
                  firefox: '50'
                }, true, ua)) {
                  Device._flag = 'firefox';
                  Device._handlerClass = _Firefox3.default;
                } // Safari (desktop and mobile) with Unified-Plan support.
                else if (_bowser.default.check({
                    safari: '12.1'
                  }, true, ua) && typeof RTCRtpTransceiver !== 'undefined' && RTCRtpTransceiver.prototype.hasOwnProperty('currentDirection')) {
                    Device._flag = 'safari';
                    Device._handlerClass = _Safari.default;
                  } // Safari (desktop and mobile) with Plab-B support.
                  else if (_bowser.default.check({
                      safari: '11'
                    }, true, ua)) {
                      Device._flag = 'safari';
                      Device._handlerClass = _Safari2.default;
                    } // Edge (desktop).
                    else if (_bowser.default.check({
                        msedge: '11'
                      }, true, ua)) {
                        Device._flag = 'msedge';
                        Device._handlerClass = _Edge.default;
                      } // Opera (desktop and mobile).
                      else if (_bowser.default.check({
                          opera: '57'
                        }, true, ua)) {
                          Device._flag = 'opera';
                          Device._handlerClass = _Chrome.default;
                        } else if (_bowser.default.check({
                          opera: '44'
                        }, true, ua)) {
                          Device._flag = 'opera';
                          Device._handlerClass = _Chrome4.default;
                        } // Best effort for Chromium based browsers.
                        else if (browser.chromium || browser.blink || browser.webkit) {
                            logger.debug('best effort Chrome based browser detection [name:"%s"]', browser.name);
                            Device._flag = 'chrome';
                            var match = ua.match(/(?:(?:Chrome|Chromium))[ /](\w+)/i);

                            if (match) {
                              var version = Number(match[1]);
                              if (version >= 70) Device._handlerClass = _Chrome.default;else if (version >= 69) Device._handlerClass = _Chrome2.default;else if (version >= 67) Device._handlerClass = _Chrome3.default;else Device._handlerClass = _Chrome4.default;
                            } else {
                              Device._handlerClass = _Chrome.default;
                            }
                          }

          if (Device.isSupported()) {
            logger.debug('browser supported [flag:%s, name:"%s", version:%s, handler:%s]', Device._flag, Device._name, Device._version, Device._handlerClass.tag);
          } else {
            logger.warn('browser not supported [name:%s, version:%s]', Device._name, Device._version);
          }
        } // Otherwise fail.
        else {
            logger.warn('device not supported');
          }
    }
  }, {
    key: "Handler",
    get: function get() {
      if (!Device._detected) Device._detect();
      return Device._handlerClass;
    }
  }]);

  return Device;
}(); // Initialized flag.
// @type {Boolean}


exports.default = Device;
Device._detected = false; // Device flag.
// @type {String}

Device._flag = undefined; // Device name.
// @type {String}

Device._name = undefined; // Device version.
// @type {String}

Device._version = undefined; // bowser module Object.
// @type {Object}

Device._bowser = undefined; // WebRTC hander for this device.
// @type {Class}

Device._handlerClass = null;
/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./../../webpack/buildin/global.js */ "./node_modules/webpack/buildin/global.js")))

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js":
/*!***********************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js ***!
  \***********************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _events = __webpack_require__(/*! events */ "./node_modules/events/events.js");

var _Logger = _interopRequireDefault(__webpack_require__(/*! ./Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var EnhancedEventEmitter =
/*#__PURE__*/
function (_EventEmitter) {
  _inherits(EnhancedEventEmitter, _EventEmitter);

  function EnhancedEventEmitter(logger) {
    var _this;

    _classCallCheck(this, EnhancedEventEmitter);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(EnhancedEventEmitter).call(this));

    _this.setMaxListeners(Infinity);

    _this._logger = logger || new _Logger.default('EnhancedEventEmitter');
    return _this;
  }

  _createClass(EnhancedEventEmitter, [{
    key: "safeEmit",
    value: function safeEmit(event) {
      try {
        for (var _len = arguments.length, args = new Array(_len > 1 ? _len - 1 : 0), _key = 1; _key < _len; _key++) {
          args[_key - 1] = arguments[_key];
        }

        this.emit.apply(this, [event].concat(args));
      } catch (error) {
        this._logger.error('safeEmit() | event listener threw an error [event:%s]:%o', event, error);
      }
    }
  }, {
    key: "safeEmitAsPromise",
    value: function safeEmitAsPromise(event) {
      var _this2 = this;

      for (var _len2 = arguments.length, args = new Array(_len2 > 1 ? _len2 - 1 : 0), _key2 = 1; _key2 < _len2; _key2++) {
        args[_key2 - 1] = arguments[_key2];
      }

      return new Promise(function (resolve, reject) {
        var callback = function callback(result) {
          resolve(result);
        };

        var errback = function errback(error) {
          _this2._logger.error('safeEmitAsPromise() | errback called [event:%s]:%o', event, error);

          reject(error);
        };

        _this2.safeEmit.apply(_this2, [event].concat(args, [callback, errback]));
      });
    }
  }]);

  return EnhancedEventEmitter;
}(_events.EventEmitter);

exports.default = EnhancedEventEmitter;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/Logger.js":
/*!*********************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/Logger.js ***!
  \*********************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _debug = _interopRequireDefault(__webpack_require__(/*! debug */ "./node_modules/debug/src/browser.js"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

var APP_NAME = 'mediasoup-client';

var Logger =
/*#__PURE__*/
function () {
  function Logger(prefix) {
    _classCallCheck(this, Logger);

    if (prefix) {
      this._debug = (0, _debug.default)("".concat(APP_NAME, ":").concat(prefix));
      this._warn = (0, _debug.default)("".concat(APP_NAME, ":WARN:").concat(prefix));
      this._error = (0, _debug.default)("".concat(APP_NAME, ":ERROR:").concat(prefix));
    } else {
      this._debug = (0, _debug.default)(APP_NAME);
      this._warn = (0, _debug.default)("".concat(APP_NAME, ":WARN"));
      this._error = (0, _debug.default)("".concat(APP_NAME, ":ERROR"));
    }
    /* eslint-disable no-console */


    this._debug.log = console.info.bind(console);
    this._warn.log = console.warn.bind(console);
    this._error.log = console.error.bind(console);
    /* eslint-enable no-console */
  }

  _createClass(Logger, [{
    key: "debug",
    get: function get() {
      return this._debug;
    }
  }, {
    key: "warn",
    get: function get() {
      return this._warn;
    }
  }, {
    key: "error",
    get: function get() {
      return this._error;
    }
  }]);

  return Logger;
}();

exports.default = Logger;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/Peer.js":
/*!*******************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/Peer.js ***!
  \*******************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _Logger = _interopRequireDefault(__webpack_require__(/*! ./Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter2 = _interopRequireDefault(__webpack_require__(/*! ./EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var logger = new _Logger.default('Peer');

var Peer =
/*#__PURE__*/
function (_EnhancedEventEmitter) {
  _inherits(Peer, _EnhancedEventEmitter);

  /**
   * @private
   *
   * @emits {consumer: Consumer} newconsumer
   * @emits {originator: String, [appData]: Any} close
   *
   * @emits @close
   */
  function Peer(name, appData) {
    var _this;

    _classCallCheck(this, Peer);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(Peer).call(this, logger)); // Name.
    // @type {String}

    _this._name = name; // Closed flag.
    // @type {Boolean}

    _this._closed = false; // App custom data.
    // @type {Any}

    _this._appData = appData; // Map of Consumers indexed by id.
    // @type {map<Number, Consumer>}

    _this._consumers = new Map();
    return _this;
  }
  /**
   * Peer name.
   *
   * @return {String}
   */


  _createClass(Peer, [{
    key: "close",

    /**
     * Closes the Peer.
     * This is called when the local Room is closed.
     *
     * @private
     */
    value: function close() {
      logger.debug('close()');
      if (this._closed) return;
      this._closed = true;
      this.emit('@close');
      this.safeEmit('close', 'local'); // Close all the Consumers.

      var _iteratorNormalCompletion = true;
      var _didIteratorError = false;
      var _iteratorError = undefined;

      try {
        for (var _iterator = this._consumers.values()[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
          var consumer = _step.value;
          consumer.close();
        }
      } catch (err) {
        _didIteratorError = true;
        _iteratorError = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion && _iterator.return != null) {
            _iterator.return();
          }
        } finally {
          if (_didIteratorError) {
            throw _iteratorError;
          }
        }
      }
    }
    /**
     * The remote Peer or Room was closed.
     * Invoked via remote notification.
     *
     * @private
     *
     * @param {Any} [appData] - App custom data.
     */

  }, {
    key: "remoteClose",
    value: function remoteClose(appData) {
      logger.debug('remoteClose()');
      if (this._closed) return;
      this._closed = true;
      this.emit('@close');
      this.safeEmit('close', 'remote', appData); // Close all the Consumers.

      var _iteratorNormalCompletion2 = true;
      var _didIteratorError2 = false;
      var _iteratorError2 = undefined;

      try {
        for (var _iterator2 = this._consumers.values()[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
          var consumer = _step2.value;
          consumer.remoteClose();
        }
      } catch (err) {
        _didIteratorError2 = true;
        _iteratorError2 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion2 && _iterator2.return != null) {
            _iterator2.return();
          }
        } finally {
          if (_didIteratorError2) {
            throw _iteratorError2;
          }
        }
      }
    }
    /**
     * Get the Consumer with the given id.
     *
     * @param {Number} id
     *
     * @return {Consumer}
     */

  }, {
    key: "getConsumerById",
    value: function getConsumerById(id) {
      return this._consumers.get(id);
    }
    /**
     * Add an associated Consumer.
     *
     * @private
     *
     * @param {Consumer} consumer
     */

  }, {
    key: "addConsumer",
    value: function addConsumer(consumer) {
      var _this2 = this;

      if (this._consumers.has(consumer.id)) throw new Error("Consumer already exists [id:".concat(consumer.id, "]")); // Store it.

      this._consumers.set(consumer.id, consumer); // Handle it.


      consumer.on('@close', function () {
        _this2._consumers.delete(consumer.id);
      }); // Emit event.

      this.safeEmit('newconsumer', consumer);
    }
  }, {
    key: "name",
    get: function get() {
      return this._name;
    }
    /**
     * Whether the Peer is closed.
     *
     * @return {Boolean}
     */

  }, {
    key: "closed",
    get: function get() {
      return this._closed;
    }
    /**
     * App custom data.
     *
     * @return {Any}
     */

  }, {
    key: "appData",
    get: function get() {
      return this._appData;
    }
    /**
     * The list of Consumers.
     *
     * @return {Array<Consumer>}
     */

  }, {
    key: "consumers",
    get: function get() {
      return Array.from(this._consumers.values());
    }
  }]);

  return Peer;
}(_EnhancedEventEmitter2.default);

exports.default = Peer;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/Producer.js":
/*!***********************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/Producer.js ***!
  \***********************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _Logger = _interopRequireDefault(__webpack_require__(/*! ./Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter2 = _interopRequireDefault(__webpack_require__(/*! ./EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

var _errors = __webpack_require__(/*! ./errors */ "./node_modules/mediasoup-client/lib-es5/errors.js");

var utils = _interopRequireWildcard(__webpack_require__(/*! ./utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var DEFAULT_STATS_INTERVAL = 1000;
var SIMULCAST_DEFAULT = {
  low: 100000,
  medium: 300000,
  high: 1500000
};
var logger = new _Logger.default('Producer');

var Producer =
/*#__PURE__*/
function (_EnhancedEventEmitter) {
  _inherits(Producer, _EnhancedEventEmitter);

  /**
   * @private
   *
   * @emits {originator: String, [appData]: Any} pause
   * @emits {originator: String, [appData]: Any} resume
   * @emits {stats: Object} stats
   * @emits handled
   * @emits unhandled
   * @emits trackended
   * @emits {originator: String, [appData]: Any} close
   *
   * @emits {originator: String, [appData]: Any} @close
   */
  function Producer(track, options, appData) {
    var _this;

    _classCallCheck(this, Producer);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(Producer).call(this, logger)); // Id.
    // @type {Number}

    _this._id = utils.randomNumber(); // Closed flag.
    // @type {Boolean}

    _this._closed = false; // Original track.
    // @type {MediaStreamTrack}

    _this._originalTrack = track; // Track cloned from the original one (if supported).
    // @type {MediaStreamTrack}

    try {
      _this._track = track.clone();
    } catch (error) {
      _this._track = track;
    } // App custom data.
    // @type {Any}


    _this._appData = appData; // Simulcast.
    // @type {Object|false}

    _this._simulcast = false;
    if (_typeof(options.simulcast) === 'object') _this._simulcast = Object.assign({}, SIMULCAST_DEFAULT, options.simulcast);else if (options.simulcast === true) _this._simulcast = Object.assign({}, SIMULCAST_DEFAULT); // Associated Transport.
    // @type {Transport}

    _this._transport = null; // RTP parameters.
    // @type {RTCRtpParameters}

    _this._rtpParameters = null; // Locally paused flag.
    // @type {Boolean}

    _this._locallyPaused = !_this._track.enabled; // Remotely paused flag.
    // @type {Boolean}

    _this._remotelyPaused = false; // Periodic stats flag.
    // @type {Boolean}

    _this._statsEnabled = false; // Periodic stats gathering interval (milliseconds).
    // @type {Number}

    _this._statsInterval = DEFAULT_STATS_INTERVAL; // Handle the effective track.

    _this._handleTrack();

    return _this;
  }
  /**
   * Producer id.
   *
   * @return {Number}
   */


  _createClass(Producer, [{
    key: "close",

    /**
     * Closes the Producer.
     *
     * @param {Any} [appData] - App custom data.
     */
    value: function close(appData) {
      logger.debug('close()');
      if (this._closed) return;
      this._closed = true;

      if (this._statsEnabled) {
        this._statsEnabled = false;

        if (this.transport) {
          this.transport.disableProducerStats(this);
        }
      }

      if (this._transport) this._transport.removeProducer(this, 'local', appData);

      this._destroy();

      this.emit('@close', 'local', appData);
      this.safeEmit('close', 'local', appData);
    }
    /**
     * My remote Producer was closed.
     * Invoked via remote notification.
     *
     * @private
     *
     * @param {Any} [appData] - App custom data.
     */

  }, {
    key: "remoteClose",
    value: function remoteClose(appData) {
      logger.debug('remoteClose()');
      if (this._closed) return;
      this._closed = true;
      if (this._transport) this._transport.removeProducer(this, 'remote', appData);

      this._destroy();

      this.emit('@close', 'remote', appData);
      this.safeEmit('close', 'remote', appData);
    }
  }, {
    key: "_destroy",
    value: function _destroy() {
      this._transport = false;
      this._rtpParameters = null;

      try {
        this._track.stop();
      } catch (error) {}
    }
    /**
     * Sends RTP.
     *
     * @param {transport} Transport instance.
     *
     * @return {Promise}
     */

  }, {
    key: "send",
    value: function send(transport) {
      var _this2 = this;

      logger.debug('send() [transport:%o]', transport);
      if (this._closed) return Promise.reject(new _errors.InvalidStateError('Producer closed'));else if (this._transport) return Promise.reject(new Error('already handled by a Transport'));else if (_typeof(transport) !== 'object') return Promise.reject(new TypeError('invalid Transport'));
      this._transport = transport;
      return transport.addProducer(this).then(function () {
        transport.once('@close', function () {
          if (_this2._closed || _this2._transport !== transport) return;

          _this2._transport.removeProducer(_this2, 'local');

          _this2._transport = null;
          _this2._rtpParameters = null;

          _this2.safeEmit('unhandled');
        });

        _this2.safeEmit('handled');

        if (_this2._statsEnabled) transport.enableProducerStats(_this2, _this2._statsInterval);
      }).catch(function (error) {
        _this2._transport = null;
        throw error;
      });
    }
    /**
     * Pauses sending media.
     *
     * @param {Any} [appData] - App custom data.
     *
     * @return {Boolean} true if paused.
     */

  }, {
    key: "pause",
    value: function pause(appData) {
      logger.debug('pause()');

      if (this._closed) {
        logger.error('pause() | Producer closed');
        return false;
      } else if (this._locallyPaused) {
        return true;
      }

      this._locallyPaused = true;
      this._track.enabled = false;
      if (this._transport) this._transport.pauseProducer(this, appData);
      this.safeEmit('pause', 'local', appData); // Return true if really paused.

      return this.paused;
    }
    /**
     * My remote Producer was paused.
     * Invoked via remote notification.
     *
     * @private
     *
     * @param {Any} [appData] - App custom data.
     */

  }, {
    key: "remotePause",
    value: function remotePause(appData) {
      logger.debug('remotePause()');
      if (this._closed || this._remotelyPaused) return;
      this._remotelyPaused = true;
      this._track.enabled = false;
      this.safeEmit('pause', 'remote', appData);
    }
    /**
     * Resumes sending media.
     *
     * @param {Any} [appData] - App custom data.
     *
     * @return {Boolean} true if not paused.
     */

  }, {
    key: "resume",
    value: function resume(appData) {
      logger.debug('resume()');

      if (this._closed) {
        logger.error('resume() | Producer closed');
        return false;
      } else if (!this._locallyPaused) {
        return true;
      }

      this._locallyPaused = false;
      if (!this._remotelyPaused) this._track.enabled = true;
      if (this._transport) this._transport.resumeProducer(this, appData);
      this.safeEmit('resume', 'local', appData); // Return true if not paused.

      return !this.paused;
    }
    /**
     * My remote Producer was resumed.
     * Invoked via remote notification.
     *
     * @private
     *
     * @param {Any} [appData] - App custom data.
     */

  }, {
    key: "remoteResume",
    value: function remoteResume(appData) {
      logger.debug('remoteResume()');
      if (this._closed || !this._remotelyPaused) return;
      this._remotelyPaused = false;
      if (!this._locallyPaused) this._track.enabled = true;
      this.safeEmit('resume', 'remote', appData);
    }
    /**
     * Replaces the current track with a new one.
     *
     * @param {MediaStreamTrack} track - New track.
     *
     * @return {Promise} Resolves with the new track itself.
     */

  }, {
    key: "replaceTrack",
    value: function replaceTrack(track) {
      var _this3 = this;

      logger.debug('replaceTrack() [track:%o]', track);
      if (this._closed) return Promise.reject(new _errors.InvalidStateError('Producer closed'));else if (!track) return Promise.reject(new TypeError('no track given'));else if (track.readyState === 'ended') return Promise.reject(new Error('track.readyState is "ended"'));
      var clonedTrack;

      try {
        clonedTrack = track.clone();
      } catch (error) {
        clonedTrack = track;
      }

      return Promise.resolve().then(function () {
        // If this Producer is handled by a Transport, we need to tell it about
        // the new track.
        if (_this3._transport) return _this3._transport.replaceProducerTrack(_this3, clonedTrack);
      }).then(function () {
        // Stop the previous track.
        try {
          _this3._track.onended = null;

          _this3._track.stop();
        } catch (error) {} // If this Producer was locally paused/resumed and the state of the new
        // track does not match, fix it.


        if (!_this3.paused) clonedTrack.enabled = true;else clonedTrack.enabled = false; // Set the new tracks.

        _this3._originalTrack = track;
        _this3._track = clonedTrack; // Handle the effective track.

        _this3._handleTrack(); // Return the new track.


        return _this3._track;
      });
    }
    /**
     * Set/update RTP parameters.
     *
     * @private
     *
     * @param {RTCRtpParameters} rtpParameters
     */

  }, {
    key: "setRtpParameters",
    value: function setRtpParameters(rtpParameters) {
      this._rtpParameters = rtpParameters;
    }
    /**
     * Enables periodic stats retrieval.
     */

  }, {
    key: "enableStats",
    value: function enableStats() {
      var interval = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : DEFAULT_STATS_INTERVAL;
      logger.debug('enableStats() [interval:%s]', interval);

      if (this._closed) {
        logger.error('enableStats() | Producer closed');
        return;
      }

      if (this._statsEnabled) return;
      if (typeof interval !== 'number' || interval < 1000) this._statsInterval = DEFAULT_STATS_INTERVAL;else this._statsInterval = interval;
      this._statsEnabled = true;
      if (this._transport) this._transport.enableProducerStats(this, this._statsInterval);
    }
    /**
     * Disables periodic stats retrieval.
     */

  }, {
    key: "disableStats",
    value: function disableStats() {
      logger.debug('disableStats()');

      if (this._closed) {
        logger.error('disableStats() | Producer closed');
        return;
      }

      if (!this._statsEnabled) return;
      this._statsEnabled = false;
      if (this._transport) this._transport.disableProducerStats(this);
    }
    /**
     * Receive remote stats.
     *
     * @private
     *
     * @param {Object} stats
     */

  }, {
    key: "remoteStats",
    value: function remoteStats(stats) {
      this.safeEmit('stats', stats);
    }
    /**
     * @private
     */

  }, {
    key: "_handleTrack",
    value: function _handleTrack() {
      var _this4 = this;

      // If the cloned track is closed (for example if the desktop sharing is closed
      // via chrome UI) notify the app and let it decide wheter to close the Producer
      // or not.
      this._track.onended = function () {
        if (_this4._closed) return;
        logger.warn('track "ended" event');

        _this4.safeEmit('trackended');
      };
    }
  }, {
    key: "id",
    get: function get() {
      return this._id;
    }
    /**
     * Whether the Producer is closed.
     *
     * @return {Boolean}
     */

  }, {
    key: "closed",
    get: function get() {
      return this._closed;
    }
    /**
     * Media kind.
     *
     * @return {String}
     */

  }, {
    key: "kind",
    get: function get() {
      return this._track.kind;
    }
    /**
     * The associated track.
     *
     * @return {MediaStreamTrack}
     */

  }, {
    key: "track",
    get: function get() {
      return this._track;
    }
    /**
     * The associated original track.
     *
     * @return {MediaStreamTrack}
     */

  }, {
    key: "originalTrack",
    get: function get() {
      return this._originalTrack;
    }
    /**
     * Simulcast settings.
     *
     * @return {Object|false}
     */

  }, {
    key: "simulcast",
    get: function get() {
      return this._simulcast;
    }
    /**
     * App custom data.
     *
     * @return {Any}
     */

  }, {
    key: "appData",
    get: function get() {
      return this._appData;
    }
    /**
     * Associated Transport.
     *
     * @return {Transport}
     */

  }, {
    key: "transport",
    get: function get() {
      return this._transport;
    }
    /**
     * RTP parameters.
     *
     * @return {RTCRtpParameters}
     */

  }, {
    key: "rtpParameters",
    get: function get() {
      return this._rtpParameters;
    }
    /**
     * Whether the Producer is locally paused.
     *
     * @return {Boolean}
     */

  }, {
    key: "locallyPaused",
    get: function get() {
      return this._locallyPaused;
    }
    /**
     * Whether the Producer is remotely paused.
     *
     * @return {Boolean}
     */

  }, {
    key: "remotelyPaused",
    get: function get() {
      return this._remotelyPaused;
    }
    /**
     * Whether the Producer is paused.
     *
     * @return {Boolean}
     */

  }, {
    key: "paused",
    get: function get() {
      return this._locallyPaused || this._remotelyPaused;
    }
  }]);

  return Producer;
}(_EnhancedEventEmitter2.default);

exports.default = Producer;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/Room.js":
/*!*******************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/Room.js ***!
  \*******************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _Logger = _interopRequireDefault(__webpack_require__(/*! ./Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter2 = _interopRequireDefault(__webpack_require__(/*! ./EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

var _errors = __webpack_require__(/*! ./errors */ "./node_modules/mediasoup-client/lib-es5/errors.js");

var ortc = _interopRequireWildcard(__webpack_require__(/*! ./ortc */ "./node_modules/mediasoup-client/lib-es5/ortc.js"));

var _Device = _interopRequireDefault(__webpack_require__(/*! ./Device */ "./node_modules/mediasoup-client/lib-es5/Device.js"));

var _Transport = _interopRequireDefault(__webpack_require__(/*! ./Transport */ "./node_modules/mediasoup-client/lib-es5/Transport.js"));

var _Producer = _interopRequireDefault(__webpack_require__(/*! ./Producer */ "./node_modules/mediasoup-client/lib-es5/Producer.js"));

var _Peer = _interopRequireDefault(__webpack_require__(/*! ./Peer */ "./node_modules/mediasoup-client/lib-es5/Peer.js"));

var _Consumer = _interopRequireDefault(__webpack_require__(/*! ./Consumer */ "./node_modules/mediasoup-client/lib-es5/Consumer.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var logger = new _Logger.default('Room');
var RoomState = {
  new: 'new',
  joining: 'joining',
  joined: 'joined',
  closed: 'closed'
};
/**
 * An instance of Room represents a remote multi conference and a local
 * peer that joins it.
 */

var Room =
/*#__PURE__*/
function (_EnhancedEventEmitter) {
  _inherits(Room, _EnhancedEventEmitter);

  /**
   * Room class.
   *
   * @param {Object} [options]
   * @param {Object} [options.roomSettings] Remote room settings, including its RTP
   * capabilities, mandatory codecs, etc. If given, no 'queryRoom' request is sent
   * to the server to discover them.
   * @param {Number} [options.requestTimeout=10000] - Timeout for sent requests
   * (in milliseconds). Defaults to 10000 (10 seconds).
   * @param {Object} [options.transportOptions] - Options for Transport created in mediasoup.
   * @param {Array<RTCIceServer>} [options.turnServers] - Array of TURN servers.
   * @param {RTCIceTransportPolicy} [options.iceTransportPolicy] - ICE transport policy.
   * @param {Boolean} [options.spy] - Whether this is a spy peer.
   *
   * @throws {Error} if device is not supported.
   *
   * @emits {request: Object, callback: Function, errback: Function} request
   * @emits {notification: Object} notify
   * @emits {peer: Peer} newpeer
   * @emits {originator: String, [appData]: Any} close
   */
  function Room(options) {
    var _this;

    _classCallCheck(this, Room);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(Room).call(this, logger));
    logger.debug('constructor() [options:%o]', options);
    if (!_Device.default.isSupported()) throw new Error('current browser/device not supported');
    options = options || {}; // Computed settings.
    // @type {Object}

    _this._settings = {
      roomSettings: options.roomSettings,
      requestTimeout: options.requestTimeout || 30000,
      transportOptions: options.transportOptions || {},
      turnServers: options.turnServers || [],
      iceTransportPolicy: options.iceTransportPolicy || 'all',
      spy: Boolean(options.spy)
    }; // Room state.
    // @type {Boolean}

    _this._state = RoomState.new; // My mediasoup Peer name.
    // @type {String}

    _this._peerName = null; // Map of Transports indexed by id.
    // @type {map<Number, Transport>}

    _this._transports = new Map(); // Map of Producers indexed by id.
    // @type {map<Number, Producer>}

    _this._producers = new Map(); // Map of Peers indexed by name.
    // @type {map<String, Peer>}

    _this._peers = new Map(); // Extended RTP capabilities.
    // @type {Object}

    _this._extendedRtpCapabilities = null; // Whether we can send audio/video based on computed extended RTP
    // capabilities.
    // @type {Object}

    _this._canSendByKind = {
      audio: false,
      video: false
    };
    return _this;
  }
  /**
   * Whether the Room is joined.
   *
   * @return {Boolean}
   */


  _createClass(Room, [{
    key: "getTransportById",

    /**
     * Get the Transport with the given id.
     *
     * @param {Number} id
     *
     * @return {Transport}
     */
    value: function getTransportById(id) {
      return this._transports.get(id);
    }
    /**
     * Get the Producer with the given id.
     *
     * @param {Number} id
     *
     * @return {Producer}
     */

  }, {
    key: "getProducerById",
    value: function getProducerById(id) {
      return this._producers.get(id);
    }
    /**
     * Get the Peer with the given name.
     *
     * @param {String} name
     *
     * @return {Peer}
     */

  }, {
    key: "getPeerByName",
    value: function getPeerByName(name) {
      return this._peers.get(name);
    }
    /**
     * Start the procedures to join a remote room.
     * @param {String} peerName - My mediasoup Peer name.
     * @param {Any} [appData] - App custom data.
     * @return {Promise}
     */

  }, {
    key: "join",
    value: function join(peerName, appData) {
      var _this2 = this;

      logger.debug('join() [peerName:"%s"]', peerName);
      if (typeof peerName !== 'string') return Promise.reject(new TypeError('invalid peerName'));

      if (this._state !== RoomState.new && this._state !== RoomState.closed) {
        return Promise.reject(new _errors.InvalidStateError("invalid state \"".concat(this._state, "\"")));
      }

      this._peerName = peerName;
      this._state = RoomState.joining;
      var roomSettings;
      return Promise.resolve().then(function () {
        // If Room settings are provided don't query them.
        if (_this2._settings.roomSettings) {
          roomSettings = _this2._settings.roomSettings;
          return;
        } else {
          return _this2._sendRequest('queryRoom', {
            target: 'room'
          }).then(function (response) {
            roomSettings = response;
            logger.debug('join() | got Room settings:%o', roomSettings);
          });
        }
      }).then(function () {
        return _Device.default.Handler.getNativeRtpCapabilities();
      }).then(function (nativeRtpCapabilities) {
        logger.debug('join() | native RTP capabilities:%o', nativeRtpCapabilities); // Get extended RTP capabilities.

        _this2._extendedRtpCapabilities = ortc.getExtendedRtpCapabilities(nativeRtpCapabilities, roomSettings.rtpCapabilities);
        logger.debug('join() | extended RTP capabilities:%o', _this2._extendedRtpCapabilities); // Check unsupported codecs.

        var unsupportedRoomCodecs = ortc.getUnsupportedCodecs(roomSettings.rtpCapabilities, roomSettings.mandatoryCodecPayloadTypes, _this2._extendedRtpCapabilities);

        if (unsupportedRoomCodecs.length > 0) {
          logger.error('%s mandatory room codecs not supported:%o', unsupportedRoomCodecs.length, unsupportedRoomCodecs);
          throw new _errors.UnsupportedError('mandatory room codecs not supported', unsupportedRoomCodecs);
        } // Check whether we can send audio/video.


        _this2._canSendByKind.audio = ortc.canSend('audio', _this2._extendedRtpCapabilities);
        _this2._canSendByKind.video = ortc.canSend('video', _this2._extendedRtpCapabilities); // Generate our effective RTP capabilities for receiving media.

        var effectiveLocalRtpCapabilities = ortc.getRtpCapabilities(_this2._extendedRtpCapabilities);
        logger.debug('join() | effective local RTP capabilities for receiving:%o', effectiveLocalRtpCapabilities);
        var data = {
          target: 'room',
          peerName: _this2._peerName,
          rtpCapabilities: effectiveLocalRtpCapabilities,
          spy: _this2._settings.spy,
          appData: appData
        };
        return _this2._sendRequest('join', data).then(function (response) {
          return response.peers;
        });
      }).then(function (peers) {
        // Handle Peers already existing in the room.
        var _iteratorNormalCompletion = true;
        var _didIteratorError = false;
        var _iteratorError = undefined;

        try {
          for (var _iterator = (peers || [])[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
            var peerData = _step.value;

            try {
              _this2._handlePeerData(peerData);
            } catch (error) {
              logger.error('join() | error handling Peer:%o', error);
            }
          }
        } catch (err) {
          _didIteratorError = true;
          _iteratorError = err;
        } finally {
          try {
            if (!_iteratorNormalCompletion && _iterator.return != null) {
              _iterator.return();
            }
          } finally {
            if (_didIteratorError) {
              throw _iteratorError;
            }
          }
        }

        _this2._state = RoomState.joined;
        logger.debug('join() | joined the Room'); // Return the list of already existing Peers.

        return _this2.peers;
      }).catch(function (error) {
        _this2._state = RoomState.new;
        throw error;
      });
    }
    /**
     * Leave the Room.
     *
     * @param {Any} [appData] - App custom data.
     */

  }, {
    key: "leave",
    value: function leave(appData) {
      logger.debug('leave()');
      if (this.closed) return; // Send a notification.

      this._sendNotification('leave', {
        appData: appData
      }); // Set closed state after sending the notification (otherwise the
      // notification won't be sent).


      this._state = RoomState.closed;
      this.safeEmit('close', 'local', appData); // Close all the Transports.

      var _iteratorNormalCompletion2 = true;
      var _didIteratorError2 = false;
      var _iteratorError2 = undefined;

      try {
        for (var _iterator2 = this._transports.values()[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
          var transport = _step2.value;
          transport.close();
        } // Close all the Producers.

      } catch (err) {
        _didIteratorError2 = true;
        _iteratorError2 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion2 && _iterator2.return != null) {
            _iterator2.return();
          }
        } finally {
          if (_didIteratorError2) {
            throw _iteratorError2;
          }
        }
      }

      var _iteratorNormalCompletion3 = true;
      var _didIteratorError3 = false;
      var _iteratorError3 = undefined;

      try {
        for (var _iterator3 = this._producers.values()[Symbol.iterator](), _step3; !(_iteratorNormalCompletion3 = (_step3 = _iterator3.next()).done); _iteratorNormalCompletion3 = true) {
          var producer = _step3.value;
          producer.close();
        } // Close all the Peers.

      } catch (err) {
        _didIteratorError3 = true;
        _iteratorError3 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion3 && _iterator3.return != null) {
            _iterator3.return();
          }
        } finally {
          if (_didIteratorError3) {
            throw _iteratorError3;
          }
        }
      }

      var _iteratorNormalCompletion4 = true;
      var _didIteratorError4 = false;
      var _iteratorError4 = undefined;

      try {
        for (var _iterator4 = this._peers.values()[Symbol.iterator](), _step4; !(_iteratorNormalCompletion4 = (_step4 = _iterator4.next()).done); _iteratorNormalCompletion4 = true) {
          var peer = _step4.value;
          peer.close();
        }
      } catch (err) {
        _didIteratorError4 = true;
        _iteratorError4 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion4 && _iterator4.return != null) {
            _iterator4.return();
          }
        } finally {
          if (_didIteratorError4) {
            throw _iteratorError4;
          }
        }
      }
    }
    /**
     * The remote Room was closed or our remote Peer has been closed.
     * Invoked via remote notification or via API.
     *
     * @param {Any} [appData] - App custom data.
     */

  }, {
    key: "remoteClose",
    value: function remoteClose(appData) {
      logger.debug('remoteClose()');
      if (this.closed) return;
      this._state = RoomState.closed;
      this.safeEmit('close', 'remote', appData); // Close all the Transports.

      var _iteratorNormalCompletion5 = true;
      var _didIteratorError5 = false;
      var _iteratorError5 = undefined;

      try {
        for (var _iterator5 = this._transports.values()[Symbol.iterator](), _step5; !(_iteratorNormalCompletion5 = (_step5 = _iterator5.next()).done); _iteratorNormalCompletion5 = true) {
          var transport = _step5.value;
          transport.remoteClose(null, {
            destroy: true
          });
        } // Close all the Producers.

      } catch (err) {
        _didIteratorError5 = true;
        _iteratorError5 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion5 && _iterator5.return != null) {
            _iterator5.return();
          }
        } finally {
          if (_didIteratorError5) {
            throw _iteratorError5;
          }
        }
      }

      var _iteratorNormalCompletion6 = true;
      var _didIteratorError6 = false;
      var _iteratorError6 = undefined;

      try {
        for (var _iterator6 = this._producers.values()[Symbol.iterator](), _step6; !(_iteratorNormalCompletion6 = (_step6 = _iterator6.next()).done); _iteratorNormalCompletion6 = true) {
          var producer = _step6.value;
          producer.remoteClose();
        } // Close all the Peers.

      } catch (err) {
        _didIteratorError6 = true;
        _iteratorError6 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion6 && _iterator6.return != null) {
            _iterator6.return();
          }
        } finally {
          if (_didIteratorError6) {
            throw _iteratorError6;
          }
        }
      }

      var _iteratorNormalCompletion7 = true;
      var _didIteratorError7 = false;
      var _iteratorError7 = undefined;

      try {
        for (var _iterator7 = this._peers.values()[Symbol.iterator](), _step7; !(_iteratorNormalCompletion7 = (_step7 = _iterator7.next()).done); _iteratorNormalCompletion7 = true) {
          var peer = _step7.value;
          peer.remoteClose();
        }
      } catch (err) {
        _didIteratorError7 = true;
        _iteratorError7 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion7 && _iterator7.return != null) {
            _iterator7.return();
          }
        } finally {
          if (_didIteratorError7) {
            throw _iteratorError7;
          }
        }
      }
    }
    /**
     * Whether we can send audio/video.
     *
     * @param {String} kind - 'audio' or 'video'.
     *
     * @return {Boolean}
     */

  }, {
    key: "canSend",
    value: function canSend(kind) {
      if (kind !== 'audio' && kind !== 'video') throw new TypeError("invalid kind \"".concat(kind, "\""));
      if (!this.joined || this._settings.spy) return false;
      return this._canSendByKind[kind];
    }
    /**
     * Creates a Transport.
     *
     * @param {String} direction - Must be 'send' or 'recv'.
     * @param {Any} [appData] - App custom data.
     *
     * @return {Transport}
     *
     * @throws {InvalidStateError} if not joined.
     * @throws {TypeError} if wrong arguments.
     */

  }, {
    key: "createTransport",
    value: function createTransport(direction, appData) {
      var _this3 = this;

      logger.debug('createTransport() [direction:%s]', direction);
      if (!this.joined) throw new _errors.InvalidStateError("invalid state \"".concat(this._state, "\""));else if (direction !== 'send' && direction !== 'recv') throw new TypeError("invalid direction \"".concat(direction, "\""));else if (direction === 'send' && this._settings.spy) throw new TypeError('a spy peer cannot send media to the room'); // Create a new Transport.

      var transport = new _Transport.default(direction, this._extendedRtpCapabilities, this._settings, appData); // Store it.

      this._transports.set(transport.id, transport);

      transport.on('@request', function (method, data, callback, errback) {
        _this3._sendRequest(method, data).then(callback).catch(errback);
      });
      transport.on('@notify', function (method, data) {
        _this3._sendNotification(method, data);
      });
      transport.on('@close', function () {
        _this3._transports.delete(transport.id);
      });
      return transport;
    }
    /**
     * Creates a Producer.
     *
     * @param {MediaStreamTrack} track
     * @param {Object} [options]
     * @param {Object} [options.simulcast]
     * @param {Any} [appData] - App custom data.
     *
     * @return {Producer}
     *
     * @throws {InvalidStateError} if not joined.
     * @throws {TypeError} if wrong arguments.
     * @throws {Error} if cannot send the given kindor we are a spy peer.
     */

  }, {
    key: "createProducer",
    value: function createProducer(track, options, appData) {
      var _this4 = this;

      logger.debug('createProducer() [track:%o, options:%o]', track, options);
      if (!this.joined) throw new _errors.InvalidStateError("invalid state \"".concat(this._state, "\""));else if (this._settings.spy) throw new Error('a spy peer cannot send media to the room');else if (!track) throw new TypeError('no track given');else if (!this._canSendByKind[track.kind]) throw new Error("cannot send ".concat(track.kind));else if (track.readyState === 'ended') throw new Error('track.readyState is "ended"');
      options = options || {}; // Create a new Producer.

      var producer = new _Producer.default(track, options, appData); // Store it.

      this._producers.set(producer.id, producer);

      producer.on('@close', function () {
        _this4._producers.delete(producer.id);
      });
      return producer;
    }
    /**
     * Produce a ICE restart in all the Transports.
     */

  }, {
    key: "restartIce",
    value: function restartIce() {
      if (!this.joined) {
        logger.warn("restartIce() | invalid state \"".concat(this._state, "\""));
        return;
      }

      var _iteratorNormalCompletion8 = true;
      var _didIteratorError8 = false;
      var _iteratorError8 = undefined;

      try {
        for (var _iterator8 = this._transports.values()[Symbol.iterator](), _step8; !(_iteratorNormalCompletion8 = (_step8 = _iterator8.next()).done); _iteratorNormalCompletion8 = true) {
          var transport = _step8.value;
          transport.restartIce();
        }
      } catch (err) {
        _didIteratorError8 = true;
        _iteratorError8 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion8 && _iterator8.return != null) {
            _iterator8.return();
          }
        } finally {
          if (_didIteratorError8) {
            throw _iteratorError8;
          }
        }
      }
    }
    /**
     * Provide the local Room with a notification generated by mediasoup server.
     *
     * @param {Object} notification
     */

  }, {
    key: "receiveNotification",
    value: function receiveNotification(notification) {
      var _this5 = this;

      if (this.closed) return Promise.reject(new _errors.InvalidStateError('Room closed'));else if (_typeof(notification) !== 'object') return Promise.reject(new TypeError('wrong notification Object'));else if (notification.notification !== true) return Promise.reject(new TypeError('not a notification'));else if (typeof notification.method !== 'string') return Promise.reject(new TypeError('wrong/missing notification method'));
      var method = notification.method;
      logger.debug('receiveNotification() [method:%s, notification:%o]', method, notification);
      return Promise.resolve().then(function () {
        switch (method) {
          case 'closed':
            {
              var appData = notification.appData;

              _this5.remoteClose(appData);

              break;
            }

          case 'transportClosed':
            {
              var id = notification.id,
                  _appData = notification.appData;

              var transport = _this5._transports.get(id);

              if (!transport) throw new Error("Transport not found [id:\"".concat(id, "\"]"));
              transport.remoteClose(_appData, {
                destroy: false
              });
              break;
            }

          case 'transportStats':
            {
              var _id = notification.id,
                  stats = notification.stats;

              var _transport = _this5._transports.get(_id);

              if (!_transport) throw new Error("Transport not found [id:".concat(_id, "]"));

              _transport.remoteStats(stats);

              break;
            }

          case 'newPeer':
            {
              var name = notification.name;
              if (_this5._peers.has(name)) throw new Error("Peer already exists [name:\"".concat(name, "\"]"));
              var peerData = notification;

              _this5._handlePeerData(peerData);

              break;
            }

          case 'peerClosed':
            {
              var peerName = notification.name;
              var _appData2 = notification.appData;

              var peer = _this5._peers.get(peerName);

              if (!peer) throw new Error("no Peer found [name:\"".concat(peerName, "\"]"));
              peer.remoteClose(_appData2);
              break;
            }

          case 'producerPaused':
            {
              var _id2 = notification.id,
                  _appData3 = notification.appData;

              var producer = _this5._producers.get(_id2);

              if (!producer) throw new Error("Producer not found [id:".concat(_id2, "]"));
              producer.remotePause(_appData3);
              break;
            }

          case 'producerResumed':
            {
              var _id3 = notification.id,
                  _appData4 = notification.appData;

              var _producer = _this5._producers.get(_id3);

              if (!_producer) throw new Error("Producer not found [id:".concat(_id3, "]"));

              _producer.remoteResume(_appData4);

              break;
            }

          case 'producerClosed':
            {
              var _id4 = notification.id,
                  _appData5 = notification.appData;

              var _producer2 = _this5._producers.get(_id4);

              if (!_producer2) throw new Error("Producer not found [id:".concat(_id4, "]"));

              _producer2.remoteClose(_appData5);

              break;
            }

          case 'producerStats':
            {
              var _id5 = notification.id,
                  _stats = notification.stats;

              var _producer3 = _this5._producers.get(_id5);

              if (!_producer3) throw new Error("Producer not found [id:".concat(_id5, "]"));

              _producer3.remoteStats(_stats);

              break;
            }

          case 'newConsumer':
            {
              var _peerName = notification.peerName;

              var _peer = _this5._peers.get(_peerName);

              if (!_peer) throw new Error("no Peer found [name:\"".concat(_peerName, "\"]"));
              var consumerData = notification;

              _this5._handleConsumerData(consumerData, _peer);

              break;
            }

          case 'consumerClosed':
            {
              var _id6 = notification.id,
                  _peerName2 = notification.peerName,
                  _appData6 = notification.appData;

              var _peer2 = _this5._peers.get(_peerName2);

              if (!_peer2) throw new Error("no Peer found [name:\"".concat(_peerName2, "\"]"));

              var consumer = _peer2.getConsumerById(_id6);

              if (!consumer) throw new Error("Consumer not found [id:".concat(_id6, "]"));
              consumer.remoteClose(_appData6);
              break;
            }

          case 'consumerPaused':
            {
              var _id7 = notification.id,
                  _peerName3 = notification.peerName,
                  _appData7 = notification.appData;

              var _peer3 = _this5._peers.get(_peerName3);

              if (!_peer3) throw new Error("no Peer found [name:\"".concat(_peerName3, "\"]"));

              var _consumer = _peer3.getConsumerById(_id7);

              if (!_consumer) throw new Error("Consumer not found [id:".concat(_id7, "]"));

              _consumer.remotePause(_appData7);

              break;
            }

          case 'consumerResumed':
            {
              var _id8 = notification.id,
                  _peerName4 = notification.peerName,
                  _appData8 = notification.appData;

              var _peer4 = _this5._peers.get(_peerName4);

              if (!_peer4) throw new Error("no Peer found [name:\"".concat(_peerName4, "\"]"));

              var _consumer2 = _peer4.getConsumerById(_id8);

              if (!_consumer2) throw new Error("Consumer not found [id:".concat(_id8, "]"));

              _consumer2.remoteResume(_appData8);

              break;
            }

          case 'consumerPreferredProfileSet':
            {
              var _id9 = notification.id,
                  _peerName5 = notification.peerName,
                  profile = notification.profile;

              var _peer5 = _this5._peers.get(_peerName5);

              if (!_peer5) throw new Error("no Peer found [name:\"".concat(_peerName5, "\"]"));

              var _consumer3 = _peer5.getConsumerById(_id9);

              if (!_consumer3) throw new Error("Consumer not found [id:".concat(_id9, "]"));

              _consumer3.remoteSetPreferredProfile(profile);

              break;
            }

          case 'consumerEffectiveProfileChanged':
            {
              var _id10 = notification.id,
                  _peerName6 = notification.peerName,
                  _profile = notification.profile;

              var _peer6 = _this5._peers.get(_peerName6);

              if (!_peer6) throw new Error("no Peer found [name:\"".concat(_peerName6, "\"]"));

              var _consumer4 = _peer6.getConsumerById(_id10);

              if (!_consumer4) throw new Error("Consumer not found [id:".concat(_id10, "]"));

              _consumer4.remoteEffectiveProfileChanged(_profile);

              break;
            }

          case 'consumerStats':
            {
              var _id11 = notification.id,
                  _peerName7 = notification.peerName,
                  _stats2 = notification.stats;

              var _peer7 = _this5._peers.get(_peerName7);

              if (!_peer7) throw new Error("no Peer found [name:\"".concat(_peerName7, "\"]"));

              var _consumer5 = _peer7.getConsumerById(_id11);

              if (!_consumer5) throw new Error("Consumer not found [id:".concat(_id11, "]"));

              _consumer5.remoteStats(_stats2);

              break;
            }

          default:
            throw new Error("unknown notification method \"".concat(method, "\""));
        }
      }).catch(function (error) {
        logger.error('receiveNotification() failed [notification:%o]: %s', notification, error);
      });
    }
  }, {
    key: "_sendRequest",
    value: function _sendRequest(method, data) {
      var _this6 = this;

      var request = Object.assign({
        method: method,
        target: 'peer'
      }, data); // Should never happen.
      // Ignore if closed.

      if (this.closed) {
        logger.error('_sendRequest() | Room closed [method:%s, request:%o]', method, request);
        return Promise.reject(new _errors.InvalidStateError('Room closed'));
      }

      logger.debug('_sendRequest() [method:%s, request:%o]', method, request);
      return new Promise(function (resolve, reject) {
        var done = false;
        var timer = setTimeout(function () {
          logger.error('request failed [method:%s]: timeout', method);
          done = true;
          reject(new _errors.TimeoutError('timeout'));
        }, _this6._settings.requestTimeout);

        var callback = function callback(response) {
          if (done) return;
          done = true;
          clearTimeout(timer);

          if (_this6.closed) {
            logger.error('request failed [method:%s]: Room closed', method);
            reject(new Error('Room closed'));
            return;
          }

          logger.debug('request succeeded [method:%s, response:%o]', method, response);
          resolve(response);
        };

        var errback = function errback(error) {
          if (done) return;
          done = true;
          clearTimeout(timer);

          if (_this6.closed) {
            logger.error('request failed [method:%s]: Room closed', method);
            reject(new Error('Room closed'));
            return;
          } // Make sure message is an Error.


          if (!(error instanceof Error)) error = new Error(String(error));
          logger.error('request failed [method:%s]:%o', method, error);
          reject(error);
        };

        _this6.safeEmit('request', request, callback, errback);
      });
    }
  }, {
    key: "_sendNotification",
    value: function _sendNotification(method, data) {
      // Ignore if closed.
      if (this.closed) return;
      var notification = Object.assign({
        method: method,
        target: 'peer',
        notification: true
      }, data);
      logger.debug('_sendNotification() [method:%s, notification:%o]', method, notification);
      this.safeEmit('notify', notification);
    }
  }, {
    key: "_handlePeerData",
    value: function _handlePeerData(peerData) {
      var _this7 = this;

      var name = peerData.name,
          consumers = peerData.consumers,
          appData = peerData.appData;
      var peer = new _Peer.default(name, appData); // Store it.

      this._peers.set(peer.name, peer);

      peer.on('@close', function () {
        _this7._peers.delete(peer.name);
      }); // Add consumers.

      var _iteratorNormalCompletion9 = true;
      var _didIteratorError9 = false;
      var _iteratorError9 = undefined;

      try {
        for (var _iterator9 = consumers[Symbol.iterator](), _step9; !(_iteratorNormalCompletion9 = (_step9 = _iterator9.next()).done); _iteratorNormalCompletion9 = true) {
          var consumerData = _step9.value;

          try {
            this._handleConsumerData(consumerData, peer);
          } catch (error) {
            logger.error('error handling existing Consumer in Peer:%o', error);
          }
        } // If already joined emit event.

      } catch (err) {
        _didIteratorError9 = true;
        _iteratorError9 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion9 && _iterator9.return != null) {
            _iterator9.return();
          }
        } finally {
          if (_didIteratorError9) {
            throw _iteratorError9;
          }
        }
      }

      if (this.joined) this.safeEmit('newpeer', peer);
    }
  }, {
    key: "_handleConsumerData",
    value: function _handleConsumerData(producerData, peer) {
      var id = producerData.id,
          kind = producerData.kind,
          rtpParameters = producerData.rtpParameters,
          paused = producerData.paused,
          appData = producerData.appData;
      var consumer = new _Consumer.default(id, kind, rtpParameters, peer, appData);
      var supported = ortc.canReceive(consumer.rtpParameters, this._extendedRtpCapabilities);
      if (supported) consumer.setSupported(true);
      if (paused) consumer.remotePause();
      peer.addConsumer(consumer);
    }
  }, {
    key: "joined",
    get: function get() {
      return this._state === RoomState.joined;
    }
    /**
     * Whether the Room is closed.
     *
     * @return {Boolean}
     */

  }, {
    key: "closed",
    get: function get() {
      return this._state === RoomState.closed;
    }
    /**
     * My mediasoup Peer name.
     *
     * @return {String}
     */

  }, {
    key: "peerName",
    get: function get() {
      return this._peerName;
    }
    /**
     * The list of Transports.
     *
     * @return {Array<Transport>}
     */

  }, {
    key: "transports",
    get: function get() {
      return Array.from(this._transports.values());
    }
    /**
     * The list of Producers.
     *
     * @return {Array<Producer>}
     */

  }, {
    key: "producers",
    get: function get() {
      return Array.from(this._producers.values());
    }
    /**
     * The list of Peers.
     *
     * @return {Array<Peer>}
     */

  }, {
    key: "peers",
    get: function get() {
      return Array.from(this._peers.values());
    }
  }]);

  return Room;
}(_EnhancedEventEmitter2.default);

exports.default = Room;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/Transport.js":
/*!************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/Transport.js ***!
  \************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _Logger = _interopRequireDefault(__webpack_require__(/*! ./Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter2 = _interopRequireDefault(__webpack_require__(/*! ./EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

var _errors = __webpack_require__(/*! ./errors */ "./node_modules/mediasoup-client/lib-es5/errors.js");

var utils = _interopRequireWildcard(__webpack_require__(/*! ./utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

var _Device = _interopRequireDefault(__webpack_require__(/*! ./Device */ "./node_modules/mediasoup-client/lib-es5/Device.js"));

var _CommandQueue = _interopRequireDefault(__webpack_require__(/*! ./CommandQueue */ "./node_modules/mediasoup-client/lib-es5/CommandQueue.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

var DEFAULT_STATS_INTERVAL = 1000;
var logger = new _Logger.default('Transport');

var Transport =
/*#__PURE__*/
function (_EnhancedEventEmitter) {
  _inherits(Transport, _EnhancedEventEmitter);

  /**
   * @private
   *
   * @emits {state: String} connectionstatechange
   * @emits {stats: Object} stats
   * @emits {originator: String, [appData]: Any} close
   *
   * @emits {method: String, [data]: Object, callback: Function, errback: Function} @request
   * @emits {method: String, [data]: Object} @notify
   * @emits @close
   */
  function Transport(direction, extendedRtpCapabilities, settings, appData) {
    var _this;

    _classCallCheck(this, Transport);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(Transport).call(this, logger));
    logger.debug('constructor() [direction:%s, extendedRtpCapabilities:%o]', direction, extendedRtpCapabilities); // Id.
    // @type {Number}

    _this._id = utils.randomNumber(); // Closed flag.
    // @type {Boolean}

    _this._closed = false; // Direction.
    // @type {String}

    _this._direction = direction; // Room settings.
    // @type {Object}

    _this._settings = settings; // App custom data.
    // @type {Any}

    _this._appData = appData; // Periodic stats flag.
    // @type {Boolean}

    _this._statsEnabled = false; // Commands handler.
    // @type {CommandQueue}

    _this._commandQueue = new _CommandQueue.default(); // Device specific handler.

    _this._handler = new _Device.default.Handler(direction, extendedRtpCapabilities, settings); // Transport state. Values can be:
    // 'new'/'connecting'/'connected'/'failed'/'disconnected'/'closed'
    // @type {String}

    _this._connectionState = 'new';

    _this._commandQueue.on('exec', _this._execCommand.bind(_assertThisInitialized(_assertThisInitialized(_this))));

    _this._handleHandler();

    return _this;
  }
  /**
   * Transport id.
   *
   * @return {Number}
   */


  _createClass(Transport, [{
    key: "close",

    /**
     * Close the Transport.
     *
     * @param {Any} [appData] - App custom data.
     */
    value: function close(appData) {
      logger.debug('close()');
      if (this._closed) return;
      this._closed = true;

      if (this._statsEnabled) {
        this._statsEnabled = false;
        this.disableStats();
      }

      this.safeEmit('@notify', 'closeTransport', {
        id: this._id,
        appData: appData
      });
      this.emit('@close');
      this.safeEmit('close', 'local', appData);

      this._destroy();
    }
    /**
     * My remote Transport was closed.
     * Invoked via remote notification.
     *
     * @private
     *
     * @param {Any} [appData] - App custom data.
     * @param {Object} destroy - Whether the local transport must be destroyed.
     */

  }, {
    key: "remoteClose",
    value: function remoteClose(appData, _ref) {
      var destroy = _ref.destroy;
      logger.debug('remoteClose() [destroy:%s]', destroy);
      if (this._closed) return;

      if (!destroy) {
        this._handler.remoteClosed();

        return;
      }

      this._closed = true;
      this.emit('@close');
      this.safeEmit('close', 'remote', appData);

      this._destroy();
    }
  }, {
    key: "_destroy",
    value: function _destroy() {
      // Close the CommandQueue.
      this._commandQueue.close(); // Close the handler.


      this._handler.close();
    }
  }, {
    key: "restartIce",
    value: function restartIce() {
      var _this2 = this;

      logger.debug('restartIce()');
      if (this._closed) return;else if (this._connectionState === 'new') return;
      Promise.resolve().then(function () {
        var data = {
          id: _this2._id
        };
        return _this2.safeEmitAsPromise('@request', 'restartTransport', data);
      }).then(function (response) {
        var remoteIceParameters = response.iceParameters; // Enqueue command.

        return _this2._commandQueue.push('restartIce', {
          remoteIceParameters: remoteIceParameters
        });
      }).catch(function (error) {
        logger.error('restartIce() | failed: %o', error);
      });
    }
  }, {
    key: "enableStats",
    value: function enableStats() {
      var interval = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : DEFAULT_STATS_INTERVAL;
      logger.debug('enableStats() [interval:%s]', interval);
      if (typeof interval !== 'number' || interval < 1000) interval = DEFAULT_STATS_INTERVAL;
      this._statsEnabled = true;
      var data = {
        id: this._id,
        interval: interval
      };
      this.safeEmit('@notify', 'enableTransportStats', data);
    }
  }, {
    key: "disableStats",
    value: function disableStats() {
      logger.debug('disableStats()');
      this._statsEnabled = false;
      var data = {
        id: this._id
      };
      this.safeEmit('@notify', 'disableTransportStats', data);
    }
  }, {
    key: "_handleHandler",
    value: function _handleHandler() {
      var _this3 = this;

      var handler = this._handler;
      handler.on('@connectionstatechange', function (state) {
        if (_this3._connectionState === state) return;
        logger.debug('Transport connection state changed to %s', state);
        _this3._connectionState = state;
        if (!_this3._closed) _this3.safeEmit('connectionstatechange', state);
      });
      handler.on('@needcreatetransport', function (transportLocalParameters, callback, errback) {
        var data = {
          id: _this3._id,
          direction: _this3._direction,
          options: _this3._settings.transportOptions,
          appData: _this3._appData
        };

        if (transportLocalParameters) {
          if (transportLocalParameters.dtlsParameters) data.dtlsParameters = transportLocalParameters.dtlsParameters;else if (transportLocalParameters.plainRtpParameters) data.plainRtpParameters = transportLocalParameters.plainRtpParameters;
        }

        _this3.safeEmit('@request', 'createTransport', data, callback, errback);
      });
      handler.on('@needupdatetransport', function (transportLocalParameters) {
        var data = {
          id: _this3._id
        };

        if (transportLocalParameters) {
          if (transportLocalParameters.dtlsParameters) data.dtlsParameters = transportLocalParameters.dtlsParameters;else if (transportLocalParameters.plainRtpParameters) data.plainRtpParameters = transportLocalParameters.plainRtpParameters;
        }

        _this3.safeEmit('@notify', 'updateTransport', data);
      });
      handler.on('@needupdateproducer', function (producer, rtpParameters) {
        var data = {
          id: producer.id,
          rtpParameters: rtpParameters
        }; // Update Producer RTP parameters.

        producer.setRtpParameters(rtpParameters); // Notify the server.

        _this3.safeEmit('@notify', 'updateProducer', data);
      });
    }
    /**
     * Send the given Producer over this Transport.
     *
     * @private
     *
     * @param {Producer} producer
     *
     * @return {Promise}
     */

  }, {
    key: "addProducer",
    value: function addProducer(producer) {
      logger.debug('addProducer() [producer:%o]', producer);
      if (this._closed) return Promise.reject(new _errors.InvalidStateError('Transport closed'));else if (this._direction !== 'send') return Promise.reject(new Error('not a sending Transport')); // Enqueue command.

      return this._commandQueue.push('addProducer', {
        producer: producer
      });
    }
    /**
     * @private
     */

  }, {
    key: "removeProducer",
    value: function removeProducer(producer, originator, appData) {
      logger.debug('removeProducer() [producer:%o]', producer); // Enqueue command.

      if (!this._closed) {
        this._commandQueue.push('removeProducer', {
          producer: producer
        }).catch(function () {});
      }

      if (originator === 'local') this.safeEmit('@notify', 'closeProducer', {
        id: producer.id,
        appData: appData
      });
    }
    /**
     * @private
     */

  }, {
    key: "pauseProducer",
    value: function pauseProducer(producer, appData) {
      logger.debug('pauseProducer() [producer:%o]', producer);
      var data = {
        id: producer.id,
        appData: appData
      };
      this.safeEmit('@notify', 'pauseProducer', data);
    }
    /**
     * @private
     */

  }, {
    key: "resumeProducer",
    value: function resumeProducer(producer, appData) {
      logger.debug('resumeProducer() [producer:%o]', producer);
      var data = {
        id: producer.id,
        appData: appData
      };
      this.safeEmit('@notify', 'resumeProducer', data);
    }
    /**
     * @private
     *
     * @return {Promise}
     */

  }, {
    key: "replaceProducerTrack",
    value: function replaceProducerTrack(producer, track) {
      logger.debug('replaceProducerTrack() [producer:%o]', producer);
      return this._commandQueue.push('replaceProducerTrack', {
        producer: producer,
        track: track
      });
    }
    /**
     * @private
     */

  }, {
    key: "enableProducerStats",
    value: function enableProducerStats(producer, interval) {
      logger.debug('enableProducerStats() [producer:%o]', producer);
      var data = {
        id: producer.id,
        interval: interval
      };
      this.safeEmit('@notify', 'enableProducerStats', data);
    }
    /**
     * @private
     */

  }, {
    key: "disableProducerStats",
    value: function disableProducerStats(producer) {
      logger.debug('disableProducerStats() [producer:%o]', producer);
      var data = {
        id: producer.id
      };
      this.safeEmit('@notify', 'disableProducerStats', data);
    }
    /**
     * Receive the given Consumer over this Transport.
     *
     * @private
     *
     * @param {Consumer} consumer
     *
     * @return {Promise} Resolves to a remote MediaStreamTrack.
     */

  }, {
    key: "addConsumer",
    value: function addConsumer(consumer) {
      logger.debug('addConsumer() [consumer:%o]', consumer);
      if (this._closed) return Promise.reject(new _errors.InvalidStateError('Transport closed'));else if (this._direction !== 'recv') return Promise.reject(new Error('not a receiving Transport')); // Enqueue command.

      return this._commandQueue.push('addConsumer', {
        consumer: consumer
      });
    }
    /**
     * @private
     */

  }, {
    key: "removeConsumer",
    value: function removeConsumer(consumer) {
      logger.debug('removeConsumer() [consumer:%o]', consumer); // Enqueue command.

      this._commandQueue.push('removeConsumer', {
        consumer: consumer
      }).catch(function () {});
    }
    /**
     * @private
     */

  }, {
    key: "pauseConsumer",
    value: function pauseConsumer(consumer, appData) {
      logger.debug('pauseConsumer() [consumer:%o]', consumer);
      var data = {
        id: consumer.id,
        appData: appData
      };
      this.safeEmit('@notify', 'pauseConsumer', data);
    }
    /**
     * @private
     */

  }, {
    key: "resumeConsumer",
    value: function resumeConsumer(consumer, appData) {
      logger.debug('resumeConsumer() [consumer:%o]', consumer);
      var data = {
        id: consumer.id,
        appData: appData
      };
      this.safeEmit('@notify', 'resumeConsumer', data);
    }
    /**
     * @private
     */

  }, {
    key: "setConsumerPreferredProfile",
    value: function setConsumerPreferredProfile(consumer, profile) {
      logger.debug('setConsumerPreferredProfile() [consumer:%o]', consumer);
      var data = {
        id: consumer.id,
        profile: profile
      };
      this.safeEmit('@notify', 'setConsumerPreferredProfile', data);
    }
    /**
     * @private
     */

  }, {
    key: "enableConsumerStats",
    value: function enableConsumerStats(consumer, interval) {
      logger.debug('enableConsumerStats() [consumer:%o]', consumer);
      var data = {
        id: consumer.id,
        interval: interval
      };
      this.safeEmit('@notify', 'enableConsumerStats', data);
    }
    /**
     * @private
     */

  }, {
    key: "disableConsumerStats",
    value: function disableConsumerStats(consumer) {
      logger.debug('disableConsumerStats() [consumer:%o]', consumer);
      var data = {
        id: consumer.id
      };
      this.safeEmit('@notify', 'disableConsumerStats', data);
    }
    /**
     * Receive remote stats.
     *
     * @private
     *
     * @param {Object} stats
     */

  }, {
    key: "remoteStats",
    value: function remoteStats(stats) {
      this.safeEmit('stats', stats);
    }
  }, {
    key: "_execCommand",
    value: function _execCommand(command, promiseHolder) {
      var promise;

      try {
        switch (command.method) {
          case 'addProducer':
            {
              var producer = command.producer;
              promise = this._execAddProducer(producer);
              break;
            }

          case 'removeProducer':
            {
              var _producer = command.producer;
              promise = this._execRemoveProducer(_producer);
              break;
            }

          case 'replaceProducerTrack':
            {
              var _producer2 = command.producer,
                  track = command.track;
              promise = this._execReplaceProducerTrack(_producer2, track);
              break;
            }

          case 'addConsumer':
            {
              var consumer = command.consumer;
              promise = this._execAddConsumer(consumer);
              break;
            }

          case 'removeConsumer':
            {
              var _consumer = command.consumer;
              promise = this._execRemoveConsumer(_consumer);
              break;
            }

          case 'restartIce':
            {
              var remoteIceParameters = command.remoteIceParameters;
              promise = this._execRestartIce(remoteIceParameters);
              break;
            }

          default:
            {
              promise = Promise.reject(new Error("unknown command method \"".concat(command.method, "\"")));
            }
        }
      } catch (error) {
        promise = Promise.reject(error);
      } // Fill the given Promise holder.


      promiseHolder.promise = promise;
    }
  }, {
    key: "_execAddProducer",
    value: function _execAddProducer(producer) {
      var _this4 = this;

      logger.debug('_execAddProducer()');
      var producerRtpParameters; // Call the handler.

      return Promise.resolve().then(function () {
        return _this4._handler.addProducer(producer);
      }).then(function (rtpParameters) {
        producerRtpParameters = rtpParameters;
        var data = {
          id: producer.id,
          kind: producer.kind,
          transportId: _this4._id,
          rtpParameters: rtpParameters,
          paused: producer.locallyPaused,
          appData: producer.appData
        };
        return _this4.safeEmitAsPromise('@request', 'createProducer', data);
      }).then(function () {
        producer.setRtpParameters(producerRtpParameters);
      });
    }
  }, {
    key: "_execRemoveProducer",
    value: function _execRemoveProducer(producer) {
      logger.debug('_execRemoveProducer()'); // Call the handler.

      return this._handler.removeProducer(producer);
    }
  }, {
    key: "_execReplaceProducerTrack",
    value: function _execReplaceProducerTrack(producer, track) {
      logger.debug('_execReplaceProducerTrack()'); // Call the handler.

      return this._handler.replaceProducerTrack(producer, track);
    }
  }, {
    key: "_execAddConsumer",
    value: function _execAddConsumer(consumer) {
      var _this5 = this;

      logger.debug('_execAddConsumer()');
      var consumerTrack; // Call the handler.

      return Promise.resolve().then(function () {
        return _this5._handler.addConsumer(consumer);
      }).then(function (track) {
        consumerTrack = track;
        var data = {
          id: consumer.id,
          transportId: _this5.id,
          paused: consumer.locallyPaused,
          preferredProfile: consumer.preferredProfile
        };
        return _this5.safeEmitAsPromise('@request', 'enableConsumer', data);
      }).then(function (response) {
        var paused = response.paused,
            preferredProfile = response.preferredProfile,
            effectiveProfile = response.effectiveProfile;
        if (paused) consumer.remotePause();
        if (preferredProfile) consumer.remoteSetPreferredProfile(preferredProfile);
        if (effectiveProfile) consumer.remoteEffectiveProfileChanged(effectiveProfile);
        return consumerTrack;
      });
    }
  }, {
    key: "_execRemoveConsumer",
    value: function _execRemoveConsumer(consumer) {
      logger.debug('_execRemoveConsumer()'); // Call the handler.

      return this._handler.removeConsumer(consumer);
    }
  }, {
    key: "_execRestartIce",
    value: function _execRestartIce(remoteIceParameters) {
      logger.debug('_execRestartIce()'); // Call the handler.

      return this._handler.restartIce(remoteIceParameters);
    }
  }, {
    key: "id",
    get: function get() {
      return this._id;
    }
    /**
     * Whether the Transport is closed.
     *
     * @return {Boolean}
     */

  }, {
    key: "closed",
    get: function get() {
      return this._closed;
    }
    /**
     * Transport direction.
     *
     * @return {String}
     */

  }, {
    key: "direction",
    get: function get() {
      return this._direction;
    }
    /**
     * App custom data.
     *
     * @return {Any}
     */

  }, {
    key: "appData",
    get: function get() {
      return this._appData;
    }
    /**
     * Connection state.
     *
     * @return {String}
     */

  }, {
    key: "connectionState",
    get: function get() {
      return this._connectionState;
    }
    /**
     * Device handler.
     *
     * @return {Handler}
     */

  }, {
    key: "handler",
    get: function get() {
      return this._handler;
    }
  }]);

  return Transport;
}(_EnhancedEventEmitter2.default);

exports.default = Transport;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/errors.js":
/*!*********************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/errors.js ***!
  \*********************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.UnsupportedError = exports.TimeoutError = exports.InvalidStateError = void 0;

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _wrapNativeSuper(Class) { var _cache = typeof Map === "function" ? new Map() : undefined; _wrapNativeSuper = function _wrapNativeSuper(Class) { if (Class === null || !_isNativeFunction(Class)) return Class; if (typeof Class !== "function") { throw new TypeError("Super expression must either be null or a function"); } if (typeof _cache !== "undefined") { if (_cache.has(Class)) return _cache.get(Class); _cache.set(Class, Wrapper); } function Wrapper() { return _construct(Class, arguments, _getPrototypeOf(this).constructor); } Wrapper.prototype = Object.create(Class.prototype, { constructor: { value: Wrapper, enumerable: false, writable: true, configurable: true } }); return _setPrototypeOf(Wrapper, Class); }; return _wrapNativeSuper(Class); }

function isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

function _construct(Parent, args, Class) { if (isNativeReflectConstruct()) { _construct = Reflect.construct; } else { _construct = function _construct(Parent, args, Class) { var a = [null]; a.push.apply(a, args); var Constructor = Function.bind.apply(Parent, a); var instance = new Constructor(); if (Class) _setPrototypeOf(instance, Class.prototype); return instance; }; } return _construct.apply(null, arguments); }

function _isNativeFunction(fn) { return Function.toString.call(fn).indexOf("[native code]") !== -1; }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

/**
 * Error produced when calling a method in an invalid state.
 */
var InvalidStateError =
/*#__PURE__*/
function (_Error) {
  _inherits(InvalidStateError, _Error);

  function InvalidStateError(message) {
    var _this;

    _classCallCheck(this, InvalidStateError);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(InvalidStateError).call(this, message));
    _this.name = 'InvalidStateError';
    if (Error.hasOwnProperty('captureStackTrace')) // Just in V8.
      Error.captureStackTrace(_assertThisInitialized(_assertThisInitialized(_this)), InvalidStateError);else _this.stack = new Error(message).stack;
    return _this;
  }

  return InvalidStateError;
}(_wrapNativeSuper(Error));
/**
 * Error produced when a Promise is rejected due to a timeout.
 */


exports.InvalidStateError = InvalidStateError;

var TimeoutError =
/*#__PURE__*/
function (_Error2) {
  _inherits(TimeoutError, _Error2);

  function TimeoutError(message) {
    var _this2;

    _classCallCheck(this, TimeoutError);

    _this2 = _possibleConstructorReturn(this, _getPrototypeOf(TimeoutError).call(this, message));
    _this2.name = 'TimeoutError';
    if (Error.hasOwnProperty('captureStackTrace')) // Just in V8.
      Error.captureStackTrace(_assertThisInitialized(_assertThisInitialized(_this2)), TimeoutError);else _this2.stack = new Error(message).stack;
    return _this2;
  }

  return TimeoutError;
}(_wrapNativeSuper(Error));
/**
 * Error indicating not support for something.
 */


exports.TimeoutError = TimeoutError;

var UnsupportedError =
/*#__PURE__*/
function (_Error3) {
  _inherits(UnsupportedError, _Error3);

  function UnsupportedError(message, data) {
    var _this3;

    _classCallCheck(this, UnsupportedError);

    _this3 = _possibleConstructorReturn(this, _getPrototypeOf(UnsupportedError).call(this, message));
    _this3.name = 'UnsupportedError';
    if (Error.hasOwnProperty('captureStackTrace')) // Just in V8.
      Error.captureStackTrace(_assertThisInitialized(_assertThisInitialized(_this3)), UnsupportedError);else _this3.stack = new Error(message).stack;
    _this3.data = data;
    return _this3;
  }

  return UnsupportedError;
}(_wrapNativeSuper(Error));

exports.UnsupportedError = UnsupportedError;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/Chrome55.js":
/*!********************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/Chrome55.js ***!
  \********************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _sdpTransform = _interopRequireDefault(__webpack_require__(/*! sdp-transform */ "./node_modules/sdp-transform/lib/index.js"));

var _Logger = _interopRequireDefault(__webpack_require__(/*! ../Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter2 = _interopRequireDefault(__webpack_require__(/*! ../EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

var utils = _interopRequireWildcard(__webpack_require__(/*! ../utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

var ortc = _interopRequireWildcard(__webpack_require__(/*! ../ortc */ "./node_modules/mediasoup-client/lib-es5/ortc.js"));

var sdpCommonUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/commonUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/commonUtils.js"));

var sdpPlanBUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/planBUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/planBUtils.js"));

var _RemotePlanBSdp = _interopRequireDefault(__webpack_require__(/*! ./sdp/RemotePlanBSdp */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemotePlanBSdp.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var logger = new _Logger.default('Chrome55');

var Handler =
/*#__PURE__*/
function (_EnhancedEventEmitter) {
  _inherits(Handler, _EnhancedEventEmitter);

  function Handler(direction, rtpParametersByKind, settings) {
    var _this;

    _classCallCheck(this, Handler);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(Handler).call(this, logger)); // RTCPeerConnection instance.
    // @type {RTCPeerConnection}

    _this._pc = new RTCPeerConnection({
      iceServers: settings.turnServers || [],
      iceTransportPolicy: settings.iceTransportPolicy,
      bundlePolicy: 'max-bundle',
      rtcpMuxPolicy: 'require'
    }); // Generic sending RTP parameters for audio and video.
    // @type {Object}

    _this._rtpParametersByKind = rtpParametersByKind; // Remote SDP handler.
    // @type {RemotePlanBSdp}

    _this._remoteSdp = new _RemotePlanBSdp.default(direction, rtpParametersByKind); // Handle RTCPeerConnection connection status.

    _this._pc.addEventListener('iceconnectionstatechange', function () {
      switch (_this._pc.iceConnectionState) {
        case 'checking':
          _this.emit('@connectionstatechange', 'connecting');

          break;

        case 'connected':
        case 'completed':
          _this.emit('@connectionstatechange', 'connected');

          break;

        case 'failed':
          _this.emit('@connectionstatechange', 'failed');

          break;

        case 'disconnected':
          _this.emit('@connectionstatechange', 'disconnected');

          break;

        case 'closed':
          _this.emit('@connectionstatechange', 'closed');

          break;
      }
    });

    return _this;
  }

  _createClass(Handler, [{
    key: "close",
    value: function close() {
      logger.debug('close()'); // Close RTCPeerConnection.

      try {
        this._pc.close();
      } catch (error) {}
    }
  }, {
    key: "remoteClosed",
    value: function remoteClosed() {
      logger.debug('remoteClosed()');
      this._transportReady = false;
      if (this._transportUpdated) this._transportUpdated = false;
    }
  }]);

  return Handler;
}(_EnhancedEventEmitter2.default);

var SendHandler =
/*#__PURE__*/
function (_Handler) {
  _inherits(SendHandler, _Handler);

  function SendHandler(rtpParametersByKind, settings) {
    var _this2;

    _classCallCheck(this, SendHandler);

    _this2 = _possibleConstructorReturn(this, _getPrototypeOf(SendHandler).call(this, 'send', rtpParametersByKind, settings)); // Got transport local and remote parameters.
    // @type {Boolean}

    _this2._transportReady = false; // Local stream.
    // @type {MediaStream}

    _this2._stream = new MediaStream();
    return _this2;
  }

  _createClass(SendHandler, [{
    key: "addProducer",
    value: function addProducer(producer) {
      var _this3 = this;

      var track = producer.track;
      logger.debug('addProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      if (this._stream.getTrackById(track.id)) return Promise.reject(new Error('track already added'));
      var localSdpObj;
      return Promise.resolve().then(function () {
        // Add the track to the local stream.
        _this3._stream.addTrack(track); // Add the stream to the PeerConnection.


        _this3._pc.addStream(_this3._stream);

        return _this3._pc.createOffer();
      }).then(function (offer) {
        // If simulcast is set, mangle the offer.
        if (producer.simulcast) {
          logger.debug('addProducer() | enabling simulcast');

          var sdpObject = _sdpTransform.default.parse(offer.sdp);

          sdpPlanBUtils.addSimulcastForTrack(sdpObject, track);

          var offerSdp = _sdpTransform.default.write(sdpObject);

          offer = {
            type: 'offer',
            sdp: offerSdp
          };
        }

        logger.debug('addProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this3._pc.setLocalDescription(offer);
      }).then(function () {
        if (!_this3._transportReady) return _this3._setupTransport();
      }).then(function () {
        localSdpObj = _sdpTransform.default.parse(_this3._pc.localDescription.sdp);

        var remoteSdp = _this3._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('addProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this3._pc.setRemoteDescription(answer);
      }).then(function () {
        var rtpParameters = utils.clone(_this3._rtpParametersByKind[producer.kind]); // Fill the RTP parameters for this track.

        sdpPlanBUtils.fillRtpParametersForTrack(rtpParameters, localSdpObj, track);
        return rtpParameters;
      }).catch(function (error) {
        // Panic here. Try to undo things.
        _this3._stream.removeTrack(track);

        _this3._pc.addStream(_this3._stream);

        throw error;
      });
    }
  }, {
    key: "removeProducer",
    value: function removeProducer(producer) {
      var _this4 = this;

      var track = producer.track;
      logger.debug('removeProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      return Promise.resolve().then(function () {
        // Remove the track from the local stream.
        _this4._stream.removeTrack(track); // Add the stream to the PeerConnection.


        _this4._pc.addStream(_this4._stream);

        return _this4._pc.createOffer();
      }).then(function (offer) {
        logger.debug('removeProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this4._pc.setLocalDescription(offer);
      }).catch(function (error) {
        // NOTE: If there are no sending tracks, setLocalDescription() will fail with
        // "Failed to create channels". If so, ignore it.
        if (_this4._stream.getTracks().length === 0) {
          logger.warn('removeProducer() | ignoring expected error due no sending tracks: %s', error.toString());
          return;
        }

        throw error;
      }).then(function () {
        if (_this4._pc.signalingState === 'stable') return;

        var localSdpObj = _sdpTransform.default.parse(_this4._pc.localDescription.sdp);

        var remoteSdp = _this4._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('removeProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this4._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "replaceProducerTrack",
    value: function replaceProducerTrack(producer, track) {
      var _this5 = this;

      logger.debug('replaceProducerTrack() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      var oldTrack = producer.track;
      var localSdpObj;
      return Promise.resolve().then(function () {
        // Remove the old track from the local stream.
        _this5._stream.removeTrack(oldTrack); // Add the new track to the local stream.


        _this5._stream.addTrack(track); // Add the stream to the PeerConnection.


        _this5._pc.addStream(_this5._stream);

        return _this5._pc.createOffer();
      }).then(function (offer) {
        // If simulcast is set, mangle the offer.
        if (producer.simulcast) {
          logger.debug('addProducer() | enabling simulcast');

          var sdpObject = _sdpTransform.default.parse(offer.sdp);

          sdpPlanBUtils.addSimulcastForTrack(sdpObject, track);

          var offerSdp = _sdpTransform.default.write(sdpObject);

          offer = {
            type: 'offer',
            sdp: offerSdp
          };
        }

        logger.debug('replaceProducerTrack() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this5._pc.setLocalDescription(offer);
      }).then(function () {
        localSdpObj = _sdpTransform.default.parse(_this5._pc.localDescription.sdp);

        var remoteSdp = _this5._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('replaceProducerTrack() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this5._pc.setRemoteDescription(answer);
      }).then(function () {
        var rtpParameters = utils.clone(_this5._rtpParametersByKind[producer.kind]); // Fill the RTP parameters for the new track.

        sdpPlanBUtils.fillRtpParametersForTrack(rtpParameters, localSdpObj, track); // We need to provide new RTP parameters.

        _this5.safeEmit('@needupdateproducer', producer, rtpParameters);
      }).catch(function (error) {
        // Panic here. Try to undo things.
        _this5._stream.removeTrack(track);

        _this5._stream.addTrack(oldTrack);

        _this5._pc.addStream(_this5._stream);

        throw error;
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this6 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        return _this6._pc.createOffer({
          iceRestart: true
        });
      }).then(function (offer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this6._pc.setLocalDescription(offer);
      }).then(function () {
        var localSdpObj = _sdpTransform.default.parse(_this6._pc.localDescription.sdp);

        var remoteSdp = _this6._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this6._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this7 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // Get our local DTLS parameters.
        var transportLocalParameters = {};
        var sdp = _this7._pc.localDescription.sdp;

        var sdpObj = _sdpTransform.default.parse(sdp);

        var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj); // Let's decide that we'll be DTLS server (because we can).

        dtlsParameters.role = 'server';
        transportLocalParameters.dtlsParameters = dtlsParameters; // Provide the remote SDP handler with transport local parameters.

        _this7._remoteSdp.setTransportLocalParameters(transportLocalParameters); // We need transport remote parameters.


        return _this7.safeEmitAsPromise('@needcreatetransport', transportLocalParameters);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this7._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this7._transportReady = true;
      });
    }
  }]);

  return SendHandler;
}(Handler);

var RecvHandler =
/*#__PURE__*/
function (_Handler2) {
  _inherits(RecvHandler, _Handler2);

  function RecvHandler(rtpParametersByKind, settings) {
    var _this8;

    _classCallCheck(this, RecvHandler);

    _this8 = _possibleConstructorReturn(this, _getPrototypeOf(RecvHandler).call(this, 'recv', rtpParametersByKind, settings)); // Got transport remote parameters.
    // @type {Boolean}

    _this8._transportCreated = false; // Got transport local parameters.
    // @type {Boolean}

    _this8._transportUpdated = false; // Seen media kinds.
    // @type {Set<String>}

    _this8._kinds = new Set(); // Map of Consumers information indexed by consumer.id.
    // - kind {String}
    // - trackId {String}
    // - ssrc {Number}
    // - rtxSsrc {Number}
    // - cname {String}
    // @type {Map<Number, Object>}

    _this8._consumerInfos = new Map();
    return _this8;
  }

  _createClass(RecvHandler, [{
    key: "addConsumer",
    value: function addConsumer(consumer) {
      var _this9 = this;

      logger.debug('addConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      if (this._consumerInfos.has(consumer.id)) return Promise.reject(new Error('Consumer already added'));
      var encoding = consumer.rtpParameters.encodings[0];
      var cname = consumer.rtpParameters.rtcp.cname;
      var consumerInfo = {
        kind: consumer.kind,
        streamId: "recv-stream-".concat(consumer.id),
        trackId: "consumer-".concat(consumer.kind, "-").concat(consumer.id),
        ssrc: encoding.ssrc,
        cname: cname
      };
      if (encoding.rtx && encoding.rtx.ssrc) consumerInfo.rtxSsrc = encoding.rtx.ssrc;

      this._consumerInfos.set(consumer.id, consumerInfo);

      this._kinds.add(consumer.kind);

      return Promise.resolve().then(function () {
        if (!_this9._transportCreated) return _this9._setupTransport();
      }).then(function () {
        var remoteSdp = _this9._remoteSdp.createOfferSdp(Array.from(_this9._kinds), Array.from(_this9._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('addConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this9._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this9._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('addConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this9._pc.setLocalDescription(answer);
      }).then(function () {
        if (!_this9._transportUpdated) return _this9._updateTransport();
      }).then(function () {
        var stream = _this9._pc.getRemoteStreams().find(function (s) {
          return s.id === consumerInfo.streamId;
        });

        var track = stream.getTrackById(consumerInfo.trackId);
        if (!track) throw new Error('remote track not found');
        return track;
      });
    }
  }, {
    key: "removeConsumer",
    value: function removeConsumer(consumer) {
      var _this10 = this;

      logger.debug('removeConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      if (!this._consumerInfos.has(consumer.id)) return Promise.reject(new Error('Consumer not found'));

      this._consumerInfos.delete(consumer.id);

      return Promise.resolve().then(function () {
        var remoteSdp = _this10._remoteSdp.createOfferSdp(Array.from(_this10._kinds), Array.from(_this10._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('removeConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this10._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this10._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('removeConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this10._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this11 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        var remoteSdp = _this11._remoteSdp.createOfferSdp(Array.from(_this11._kinds), Array.from(_this11._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this11._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this11._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this11._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this12 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // We need transport remote parameters.
        return _this12.safeEmitAsPromise('@needcreatetransport', null);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this12._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this12._transportCreated = true;
      });
    }
  }, {
    key: "_updateTransport",
    value: function _updateTransport() {
      logger.debug('_updateTransport()'); // Get our local DTLS parameters.

      var sdp = this._pc.localDescription.sdp;

      var sdpObj = _sdpTransform.default.parse(sdp);

      var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj);
      var transportLocalParameters = {
        dtlsParameters: dtlsParameters
      }; // We need to provide transport local parameters.

      this.safeEmit('@needupdatetransport', transportLocalParameters);
      this._transportUpdated = true;
    }
  }]);

  return RecvHandler;
}(Handler);

var Chrome55 =
/*#__PURE__*/
function () {
  _createClass(Chrome55, null, [{
    key: "getNativeRtpCapabilities",
    value: function getNativeRtpCapabilities() {
      logger.debug('getNativeRtpCapabilities()');
      var pc = new RTCPeerConnection({
        iceServers: [],
        iceTransportPolicy: 'all',
        bundlePolicy: 'max-bundle',
        rtcpMuxPolicy: 'require'
      });
      return pc.createOffer({
        offerToReceiveAudio: true,
        offerToReceiveVideo: true
      }).then(function (offer) {
        try {
          pc.close();
        } catch (error) {}

        var sdpObj = _sdpTransform.default.parse(offer.sdp);

        var nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities(sdpObj);
        return nativeRtpCapabilities;
      }).catch(function (error) {
        try {
          pc.close();
        } catch (error2) {}

        throw error;
      });
    }
  }, {
    key: "tag",
    get: function get() {
      return 'Chrome55';
    }
  }]);

  function Chrome55(direction, extendedRtpCapabilities, settings) {
    _classCallCheck(this, Chrome55);

    logger.debug('constructor() [direction:%s, extendedRtpCapabilities:%o]', direction, extendedRtpCapabilities);
    var rtpParametersByKind;

    switch (direction) {
      case 'send':
        {
          rtpParametersByKind = {
            audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
          };
          return new SendHandler(rtpParametersByKind, settings);
        }

      case 'recv':
        {
          rtpParametersByKind = {
            audio: ortc.getReceivingFullRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getReceivingFullRtpParameters('video', extendedRtpCapabilities)
          };
          return new RecvHandler(rtpParametersByKind, settings);
        }
    }
  }

  return Chrome55;
}();

exports.default = Chrome55;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/Chrome67.js":
/*!********************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/Chrome67.js ***!
  \********************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _sdpTransform = _interopRequireDefault(__webpack_require__(/*! sdp-transform */ "./node_modules/sdp-transform/lib/index.js"));

var _Logger = _interopRequireDefault(__webpack_require__(/*! ../Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter2 = _interopRequireDefault(__webpack_require__(/*! ../EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

var utils = _interopRequireWildcard(__webpack_require__(/*! ../utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

var ortc = _interopRequireWildcard(__webpack_require__(/*! ../ortc */ "./node_modules/mediasoup-client/lib-es5/ortc.js"));

var sdpCommonUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/commonUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/commonUtils.js"));

var sdpPlanBUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/planBUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/planBUtils.js"));

var _RemotePlanBSdp = _interopRequireDefault(__webpack_require__(/*! ./sdp/RemotePlanBSdp */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemotePlanBSdp.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var logger = new _Logger.default('Chrome67');

var Handler =
/*#__PURE__*/
function (_EnhancedEventEmitter) {
  _inherits(Handler, _EnhancedEventEmitter);

  function Handler(direction, rtpParametersByKind, settings) {
    var _this;

    _classCallCheck(this, Handler);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(Handler).call(this, logger)); // RTCPeerConnection instance.
    // @type {RTCPeerConnection}

    _this._pc = new RTCPeerConnection({
      iceServers: settings.turnServers || [],
      iceTransportPolicy: settings.iceTransportPolicy,
      bundlePolicy: 'max-bundle',
      rtcpMuxPolicy: 'require',
      sdpSemantics: 'plan-b'
    }); // Generic sending RTP parameters for audio and video.
    // @type {Object}

    _this._rtpParametersByKind = rtpParametersByKind; // Remote SDP handler.
    // @type {RemotePlanBSdp}

    _this._remoteSdp = new _RemotePlanBSdp.default(direction, rtpParametersByKind); // Handle RTCPeerConnection connection status.

    _this._pc.addEventListener('iceconnectionstatechange', function () {
      switch (_this._pc.iceConnectionState) {
        case 'checking':
          _this.emit('@connectionstatechange', 'connecting');

          break;

        case 'connected':
        case 'completed':
          _this.emit('@connectionstatechange', 'connected');

          break;

        case 'failed':
          _this.emit('@connectionstatechange', 'failed');

          break;

        case 'disconnected':
          _this.emit('@connectionstatechange', 'disconnected');

          break;

        case 'closed':
          _this.emit('@connectionstatechange', 'closed');

          break;
      }
    });

    return _this;
  }

  _createClass(Handler, [{
    key: "close",
    value: function close() {
      logger.debug('close()'); // Close RTCPeerConnection.

      try {
        this._pc.close();
      } catch (error) {}
    }
  }, {
    key: "remoteClosed",
    value: function remoteClosed() {
      logger.debug('remoteClosed()');
      this._transportReady = false;
      if (this._transportUpdated) this._transportUpdated = false;
    }
  }]);

  return Handler;
}(_EnhancedEventEmitter2.default);

var SendHandler =
/*#__PURE__*/
function (_Handler) {
  _inherits(SendHandler, _Handler);

  function SendHandler(rtpParametersByKind, settings) {
    var _this2;

    _classCallCheck(this, SendHandler);

    _this2 = _possibleConstructorReturn(this, _getPrototypeOf(SendHandler).call(this, 'send', rtpParametersByKind, settings)); // Got transport local and remote parameters.
    // @type {Boolean}

    _this2._transportReady = false; // Local stream.
    // @type {MediaStream}

    _this2._stream = new MediaStream();
    return _this2;
  }

  _createClass(SendHandler, [{
    key: "addProducer",
    value: function addProducer(producer) {
      var _this3 = this;

      var track = producer.track;
      logger.debug('addProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      if (this._stream.getTrackById(track.id)) return Promise.reject(new Error('track already added'));
      var localSdpObj;
      return Promise.resolve().then(function () {
        // Add the track to the local stream.
        _this3._stream.addTrack(track); // Add the stream to the PeerConnection.


        _this3._pc.addStream(_this3._stream);

        return _this3._pc.createOffer();
      }).then(function (offer) {
        // If simulcast is set, mangle the offer.
        if (producer.simulcast) {
          logger.debug('addProducer() | enabling simulcast');

          var sdpObject = _sdpTransform.default.parse(offer.sdp);

          sdpPlanBUtils.addSimulcastForTrack(sdpObject, track);

          var offerSdp = _sdpTransform.default.write(sdpObject);

          offer = {
            type: 'offer',
            sdp: offerSdp
          };
        }

        logger.debug('addProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this3._pc.setLocalDescription(offer);
      }).then(function () {
        if (!_this3._transportReady) return _this3._setupTransport();
      }).then(function () {
        localSdpObj = _sdpTransform.default.parse(_this3._pc.localDescription.sdp);

        var remoteSdp = _this3._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('addProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this3._pc.setRemoteDescription(answer);
      }).then(function () {
        var rtpParameters = utils.clone(_this3._rtpParametersByKind[producer.kind]); // Fill the RTP parameters for this track.

        sdpPlanBUtils.fillRtpParametersForTrack(rtpParameters, localSdpObj, track);
        return rtpParameters;
      }).catch(function (error) {
        // Panic here. Try to undo things.
        _this3._stream.removeTrack(track);

        _this3._pc.addStream(_this3._stream);

        throw error;
      });
    }
  }, {
    key: "removeProducer",
    value: function removeProducer(producer) {
      var _this4 = this;

      var track = producer.track;
      logger.debug('removeProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      return Promise.resolve().then(function () {
        // Remove the track from the local stream.
        _this4._stream.removeTrack(track); // Add the stream to the PeerConnection.


        _this4._pc.addStream(_this4._stream);

        return _this4._pc.createOffer();
      }).then(function (offer) {
        logger.debug('removeProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this4._pc.setLocalDescription(offer);
      }).catch(function (error) {
        // NOTE: If there are no sending tracks, setLocalDescription() will fail with
        // "Failed to create channels". If so, ignore it.
        if (_this4._stream.getTracks().length === 0) {
          logger.warn('removeProducer() | ignoring expected error due no sending tracks: %s', error.toString());
          return;
        }

        throw error;
      }).then(function () {
        if (_this4._pc.signalingState === 'stable') return;

        var localSdpObj = _sdpTransform.default.parse(_this4._pc.localDescription.sdp);

        var remoteSdp = _this4._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('removeProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this4._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "replaceProducerTrack",
    value: function replaceProducerTrack(producer, track) {
      var _this5 = this;

      logger.debug('replaceProducerTrack() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      var oldTrack = producer.track;
      return Promise.resolve().then(function () {
        // Get the associated RTCRtpSender.
        var rtpSender = _this5._pc.getSenders().find(function (s) {
          return s.track === oldTrack;
        });

        if (!rtpSender) throw new Error('local track not found');
        return rtpSender.replaceTrack(track);
      }).then(function () {
        // Remove the old track from the local stream.
        _this5._stream.removeTrack(oldTrack); // Add the new track to the local stream.


        _this5._stream.addTrack(track);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this6 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        return _this6._pc.createOffer({
          iceRestart: true
        });
      }).then(function (offer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this6._pc.setLocalDescription(offer);
      }).then(function () {
        var localSdpObj = _sdpTransform.default.parse(_this6._pc.localDescription.sdp);

        var remoteSdp = _this6._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this6._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this7 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // Get our local DTLS parameters.
        var transportLocalParameters = {};
        var sdp = _this7._pc.localDescription.sdp;

        var sdpObj = _sdpTransform.default.parse(sdp);

        var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj); // Let's decide that we'll be DTLS server (because we can).

        dtlsParameters.role = 'server';
        transportLocalParameters.dtlsParameters = dtlsParameters; // Provide the remote SDP handler with transport local parameters.

        _this7._remoteSdp.setTransportLocalParameters(transportLocalParameters); // We need transport remote parameters.


        return _this7.safeEmitAsPromise('@needcreatetransport', transportLocalParameters);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this7._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this7._transportReady = true;
      });
    }
  }]);

  return SendHandler;
}(Handler);

var RecvHandler =
/*#__PURE__*/
function (_Handler2) {
  _inherits(RecvHandler, _Handler2);

  function RecvHandler(rtpParametersByKind, settings) {
    var _this8;

    _classCallCheck(this, RecvHandler);

    _this8 = _possibleConstructorReturn(this, _getPrototypeOf(RecvHandler).call(this, 'recv', rtpParametersByKind, settings)); // Got transport remote parameters.
    // @type {Boolean}

    _this8._transportCreated = false; // Got transport local parameters.
    // @type {Boolean}

    _this8._transportUpdated = false; // Seen media kinds.
    // @type {Set<String>}

    _this8._kinds = new Set(); // Map of Consumers information indexed by consumer.id.
    // - kind {String}
    // - trackId {String}
    // - ssrc {Number}
    // - rtxSsrc {Number}
    // - cname {String}
    // @type {Map<Number, Object>}

    _this8._consumerInfos = new Map();
    return _this8;
  }

  _createClass(RecvHandler, [{
    key: "addConsumer",
    value: function addConsumer(consumer) {
      var _this9 = this;

      logger.debug('addConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      if (this._consumerInfos.has(consumer.id)) return Promise.reject(new Error('Consumer already added'));
      var encoding = consumer.rtpParameters.encodings[0];
      var cname = consumer.rtpParameters.rtcp.cname;
      var consumerInfo = {
        kind: consumer.kind,
        streamId: "recv-stream-".concat(consumer.id),
        trackId: "consumer-".concat(consumer.kind, "-").concat(consumer.id),
        ssrc: encoding.ssrc,
        cname: cname
      };
      if (encoding.rtx && encoding.rtx.ssrc) consumerInfo.rtxSsrc = encoding.rtx.ssrc;

      this._consumerInfos.set(consumer.id, consumerInfo);

      this._kinds.add(consumer.kind);

      return Promise.resolve().then(function () {
        if (!_this9._transportCreated) return _this9._setupTransport();
      }).then(function () {
        var remoteSdp = _this9._remoteSdp.createOfferSdp(Array.from(_this9._kinds), Array.from(_this9._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('addConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this9._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this9._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('addConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this9._pc.setLocalDescription(answer);
      }).then(function () {
        if (!_this9._transportUpdated) return _this9._updateTransport();
      }).then(function () {
        var stream = _this9._pc.getRemoteStreams().find(function (s) {
          return s.id === consumerInfo.streamId;
        });

        var track = stream.getTrackById(consumerInfo.trackId);
        if (!track) throw new Error('remote track not found');
        return track;
      });
    }
  }, {
    key: "removeConsumer",
    value: function removeConsumer(consumer) {
      var _this10 = this;

      logger.debug('removeConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      if (!this._consumerInfos.has(consumer.id)) return Promise.reject(new Error('Consumer not found'));

      this._consumerInfos.delete(consumer.id);

      return Promise.resolve().then(function () {
        var remoteSdp = _this10._remoteSdp.createOfferSdp(Array.from(_this10._kinds), Array.from(_this10._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('removeConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this10._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this10._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('removeConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this10._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this11 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        var remoteSdp = _this11._remoteSdp.createOfferSdp(Array.from(_this11._kinds), Array.from(_this11._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this11._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this11._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this11._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this12 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // We need transport remote parameters.
        return _this12.safeEmitAsPromise('@needcreatetransport', null);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this12._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this12._transportCreated = true;
      });
    }
  }, {
    key: "_updateTransport",
    value: function _updateTransport() {
      logger.debug('_updateTransport()'); // Get our local DTLS parameters.

      var sdp = this._pc.localDescription.sdp;

      var sdpObj = _sdpTransform.default.parse(sdp);

      var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj);
      var transportLocalParameters = {
        dtlsParameters: dtlsParameters
      }; // We need to provide transport local parameters.

      this.safeEmit('@needupdatetransport', transportLocalParameters);
      this._transportUpdated = true;
    }
  }]);

  return RecvHandler;
}(Handler);

var Chrome67 =
/*#__PURE__*/
function () {
  _createClass(Chrome67, null, [{
    key: "getNativeRtpCapabilities",
    value: function getNativeRtpCapabilities() {
      logger.debug('getNativeRtpCapabilities()');
      var pc = new RTCPeerConnection({
        iceServers: [],
        iceTransportPolicy: 'all',
        bundlePolicy: 'max-bundle',
        rtcpMuxPolicy: 'require',
        sdpSemantics: 'plan-b'
      });
      return pc.createOffer({
        offerToReceiveAudio: true,
        offerToReceiveVideo: true
      }).then(function (offer) {
        try {
          pc.close();
        } catch (error) {}

        var sdpObj = _sdpTransform.default.parse(offer.sdp);

        var nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities(sdpObj);
        return nativeRtpCapabilities;
      }).catch(function (error) {
        try {
          pc.close();
        } catch (error2) {}

        throw error;
      });
    }
  }, {
    key: "tag",
    get: function get() {
      return 'Chrome67';
    }
  }]);

  function Chrome67(direction, extendedRtpCapabilities, settings) {
    _classCallCheck(this, Chrome67);

    logger.debug('constructor() [direction:%s, extendedRtpCapabilities:%o]', direction, extendedRtpCapabilities);
    var rtpParametersByKind;

    switch (direction) {
      case 'send':
        {
          rtpParametersByKind = {
            audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
          };
          return new SendHandler(rtpParametersByKind, settings);
        }

      case 'recv':
        {
          rtpParametersByKind = {
            audio: ortc.getReceivingFullRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getReceivingFullRtpParameters('video', extendedRtpCapabilities)
          };
          return new RecvHandler(rtpParametersByKind, settings);
        }
    }
  }

  return Chrome67;
}();

exports.default = Chrome67;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/Chrome69.js":
/*!********************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/Chrome69.js ***!
  \********************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _sdpTransform = _interopRequireDefault(__webpack_require__(/*! sdp-transform */ "./node_modules/sdp-transform/lib/index.js"));

var _Logger = _interopRequireDefault(__webpack_require__(/*! ../Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter2 = _interopRequireDefault(__webpack_require__(/*! ../EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

var utils = _interopRequireWildcard(__webpack_require__(/*! ../utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

var ortc = _interopRequireWildcard(__webpack_require__(/*! ../ortc */ "./node_modules/mediasoup-client/lib-es5/ortc.js"));

var sdpCommonUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/commonUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/commonUtils.js"));

var sdpPlanBUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/planBUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/planBUtils.js"));

var _RemotePlanBSdp = _interopRequireDefault(__webpack_require__(/*! ./sdp/RemotePlanBSdp */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemotePlanBSdp.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var logger = new _Logger.default('Chrome69');

var Handler =
/*#__PURE__*/
function (_EnhancedEventEmitter) {
  _inherits(Handler, _EnhancedEventEmitter);

  function Handler(direction, rtpParametersByKind, settings) {
    var _this;

    _classCallCheck(this, Handler);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(Handler).call(this, logger)); // RTCPeerConnection instance.
    // @type {RTCPeerConnection}

    _this._pc = new RTCPeerConnection({
      iceServers: settings.turnServers || [],
      iceTransportPolicy: settings.iceTransportPolicy,
      bundlePolicy: 'max-bundle',
      rtcpMuxPolicy: 'require',
      sdpSemantics: 'plan-b'
    }); // Generic sending RTP parameters for audio and video.
    // @type {Object}

    _this._rtpParametersByKind = rtpParametersByKind; // Remote SDP handler.
    // @type {RemotePlanBSdp}

    _this._remoteSdp = new _RemotePlanBSdp.default(direction, rtpParametersByKind); // Handle RTCPeerConnection connection status.

    _this._pc.addEventListener('iceconnectionstatechange', function () {
      switch (_this._pc.iceConnectionState) {
        case 'checking':
          _this.emit('@connectionstatechange', 'connecting');

          break;

        case 'connected':
        case 'completed':
          _this.emit('@connectionstatechange', 'connected');

          break;

        case 'failed':
          _this.emit('@connectionstatechange', 'failed');

          break;

        case 'disconnected':
          _this.emit('@connectionstatechange', 'disconnected');

          break;

        case 'closed':
          _this.emit('@connectionstatechange', 'closed');

          break;
      }
    });

    return _this;
  }

  _createClass(Handler, [{
    key: "close",
    value: function close() {
      logger.debug('close()'); // Close RTCPeerConnection.

      try {
        this._pc.close();
      } catch (error) {}
    }
  }, {
    key: "remoteClosed",
    value: function remoteClosed() {
      logger.debug('remoteClosed()');
      this._transportReady = false;
      if (this._transportUpdated) this._transportUpdated = false;
    }
  }]);

  return Handler;
}(_EnhancedEventEmitter2.default);

var SendHandler =
/*#__PURE__*/
function (_Handler) {
  _inherits(SendHandler, _Handler);

  function SendHandler(rtpParametersByKind, settings) {
    var _this2;

    _classCallCheck(this, SendHandler);

    _this2 = _possibleConstructorReturn(this, _getPrototypeOf(SendHandler).call(this, 'send', rtpParametersByKind, settings)); // Got transport local and remote parameters.
    // @type {Boolean}

    _this2._transportReady = false; // Local stream.
    // @type {MediaStream}

    _this2._stream = new MediaStream();
    return _this2;
  }

  _createClass(SendHandler, [{
    key: "addProducer",
    value: function addProducer(producer) {
      var _this3 = this;

      var track = producer.track;
      logger.debug('addProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      if (this._stream.getTrackById(track.id)) return Promise.reject(new Error('track already added'));
      var rtpSender;
      var localSdpObj;
      return Promise.resolve().then(function () {
        // Add the track to the local stream.
        _this3._stream.addTrack(track); // Add the stream to the PeerConnection.


        rtpSender = _this3._pc.addTrack(track, _this3._stream);
        return _this3._pc.createOffer();
      }).then(function (offer) {
        // If simulcast is set, mangle the offer.
        if (producer.simulcast) {
          logger.debug('addProducer() | enabling simulcast');

          var sdpObject = _sdpTransform.default.parse(offer.sdp);

          sdpPlanBUtils.addSimulcastForTrack(sdpObject, track);

          var offerSdp = _sdpTransform.default.write(sdpObject);

          offer = {
            type: 'offer',
            sdp: offerSdp
          };
        }

        logger.debug('addProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this3._pc.setLocalDescription(offer);
      }).then(function () {
        if (!_this3._transportReady) return _this3._setupTransport();
      }).then(function () {
        localSdpObj = _sdpTransform.default.parse(_this3._pc.localDescription.sdp);

        var remoteSdp = _this3._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('addProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this3._pc.setRemoteDescription(answer);
      }).then(function () {
        var rtpParameters = utils.clone(_this3._rtpParametersByKind[producer.kind]); // Fill the RTP parameters for this track.

        sdpPlanBUtils.fillRtpParametersForTrack(rtpParameters, localSdpObj, track);
        return rtpParameters;
      }).catch(function (error) {
        // Panic here. Try to undo things.
        try {
          _this3._pc.removeTrack(rtpSender);
        } catch (error2) {}

        _this3._stream.removeTrack(track);

        throw error;
      });
    }
  }, {
    key: "removeProducer",
    value: function removeProducer(producer) {
      var _this4 = this;

      var track = producer.track;
      logger.debug('removeProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      return Promise.resolve().then(function () {
        // Get the associated RTCRtpSender.
        var rtpSender = _this4._pc.getSenders().find(function (s) {
          return s.track === track;
        });

        if (!rtpSender) throw new Error('RTCRtpSender not found'); // Remove the associated RtpSender.

        _this4._pc.removeTrack(rtpSender); // Remove the track from the local stream.


        _this4._stream.removeTrack(track);

        return _this4._pc.createOffer();
      }).then(function (offer) {
        logger.debug('removeProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this4._pc.setLocalDescription(offer);
      }).catch(function (error) {
        // NOTE: If there are no sending tracks, setLocalDescription() will fail with
        // "Failed to create channels". If so, ignore it.
        if (_this4._stream.getTracks().length === 0) {
          logger.warn('removeProducer() | ignoring expected error due no sending tracks: %s', error.toString());
          return;
        }

        throw error;
      }).then(function () {
        if (_this4._pc.signalingState === 'stable') return;

        var localSdpObj = _sdpTransform.default.parse(_this4._pc.localDescription.sdp);

        var remoteSdp = _this4._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('removeProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this4._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "replaceProducerTrack",
    value: function replaceProducerTrack(producer, track) {
      var _this5 = this;

      logger.debug('replaceProducerTrack() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      var oldTrack = producer.track;
      return Promise.resolve().then(function () {
        // Get the associated RTCRtpSender.
        var rtpSender = _this5._pc.getSenders().find(function (s) {
          return s.track === oldTrack;
        });

        if (!rtpSender) throw new Error('local track not found');
        return rtpSender.replaceTrack(track);
      }).then(function () {
        // Remove the old track from the local stream.
        _this5._stream.removeTrack(oldTrack); // Add the new track to the local stream.


        _this5._stream.addTrack(track);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this6 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        return _this6._pc.createOffer({
          iceRestart: true
        });
      }).then(function (offer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this6._pc.setLocalDescription(offer);
      }).then(function () {
        var localSdpObj = _sdpTransform.default.parse(_this6._pc.localDescription.sdp);

        var remoteSdp = _this6._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this6._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this7 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // Get our local DTLS parameters.
        var transportLocalParameters = {};
        var sdp = _this7._pc.localDescription.sdp;

        var sdpObj = _sdpTransform.default.parse(sdp);

        var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj); // Let's decide that we'll be DTLS server (because we can).

        dtlsParameters.role = 'server';
        transportLocalParameters.dtlsParameters = dtlsParameters; // Provide the remote SDP handler with transport local parameters.

        _this7._remoteSdp.setTransportLocalParameters(transportLocalParameters); // We need transport remote parameters.


        return _this7.safeEmitAsPromise('@needcreatetransport', transportLocalParameters);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this7._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this7._transportReady = true;
      });
    }
  }]);

  return SendHandler;
}(Handler);

var RecvHandler =
/*#__PURE__*/
function (_Handler2) {
  _inherits(RecvHandler, _Handler2);

  function RecvHandler(rtpParametersByKind, settings) {
    var _this8;

    _classCallCheck(this, RecvHandler);

    _this8 = _possibleConstructorReturn(this, _getPrototypeOf(RecvHandler).call(this, 'recv', rtpParametersByKind, settings)); // Got transport remote parameters.
    // @type {Boolean}

    _this8._transportCreated = false; // Got transport local parameters.
    // @type {Boolean}

    _this8._transportUpdated = false; // Seen media kinds.
    // @type {Set<String>}

    _this8._kinds = new Set(); // Map of Consumers information indexed by consumer.id.
    // - kind {String}
    // - trackId {String}
    // - ssrc {Number}
    // - rtxSsrc {Number}
    // - cname {String}
    // @type {Map<Number, Object>}

    _this8._consumerInfos = new Map();
    return _this8;
  }

  _createClass(RecvHandler, [{
    key: "addConsumer",
    value: function addConsumer(consumer) {
      var _this9 = this;

      logger.debug('addConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      if (this._consumerInfos.has(consumer.id)) return Promise.reject(new Error('Consumer already added'));
      var encoding = consumer.rtpParameters.encodings[0];
      var cname = consumer.rtpParameters.rtcp.cname;
      var consumerInfo = {
        kind: consumer.kind,
        streamId: "recv-stream-".concat(consumer.id),
        trackId: "consumer-".concat(consumer.kind, "-").concat(consumer.id),
        ssrc: encoding.ssrc,
        cname: cname
      };
      if (encoding.rtx && encoding.rtx.ssrc) consumerInfo.rtxSsrc = encoding.rtx.ssrc;

      this._consumerInfos.set(consumer.id, consumerInfo);

      this._kinds.add(consumer.kind);

      return Promise.resolve().then(function () {
        if (!_this9._transportCreated) return _this9._setupTransport();
      }).then(function () {
        var remoteSdp = _this9._remoteSdp.createOfferSdp(Array.from(_this9._kinds), Array.from(_this9._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('addConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this9._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this9._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('addConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this9._pc.setLocalDescription(answer);
      }).then(function () {
        if (!_this9._transportUpdated) return _this9._updateTransport();
      }).then(function () {
        var newRtpReceiver = _this9._pc.getReceivers().find(function (rtpReceiver) {
          var track = rtpReceiver.track;
          if (!track) return false;
          return track.id === consumerInfo.trackId;
        });

        if (!newRtpReceiver) throw new Error('remote track not found');
        return newRtpReceiver.track;
      });
    }
  }, {
    key: "removeConsumer",
    value: function removeConsumer(consumer) {
      var _this10 = this;

      logger.debug('removeConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      if (!this._consumerInfos.has(consumer.id)) return Promise.reject(new Error('Consumer not found'));

      this._consumerInfos.delete(consumer.id);

      return Promise.resolve().then(function () {
        var remoteSdp = _this10._remoteSdp.createOfferSdp(Array.from(_this10._kinds), Array.from(_this10._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('removeConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this10._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this10._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('removeConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this10._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this11 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        var remoteSdp = _this11._remoteSdp.createOfferSdp(Array.from(_this11._kinds), Array.from(_this11._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this11._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this11._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this11._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this12 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // We need transport remote parameters.
        return _this12.safeEmitAsPromise('@needcreatetransport', null);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this12._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this12._transportCreated = true;
      });
    }
  }, {
    key: "_updateTransport",
    value: function _updateTransport() {
      logger.debug('_updateTransport()'); // Get our local DTLS parameters.

      var sdp = this._pc.localDescription.sdp;

      var sdpObj = _sdpTransform.default.parse(sdp);

      var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj);
      var transportLocalParameters = {
        dtlsParameters: dtlsParameters
      }; // We need to provide transport local parameters.

      this.safeEmit('@needupdatetransport', transportLocalParameters);
      this._transportUpdated = true;
    }
  }]);

  return RecvHandler;
}(Handler);

var Chrome69 =
/*#__PURE__*/
function () {
  _createClass(Chrome69, null, [{
    key: "getNativeRtpCapabilities",
    value: function getNativeRtpCapabilities() {
      logger.debug('getNativeRtpCapabilities()');
      var pc = new RTCPeerConnection({
        iceServers: [],
        iceTransportPolicy: 'all',
        bundlePolicy: 'max-bundle',
        rtcpMuxPolicy: 'require',
        sdpSemantics: 'plan-b'
      });
      return pc.createOffer({
        offerToReceiveAudio: true,
        offerToReceiveVideo: true
      }).then(function (offer) {
        try {
          pc.close();
        } catch (error) {}

        var sdpObj = _sdpTransform.default.parse(offer.sdp);

        var nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities(sdpObj);
        return nativeRtpCapabilities;
      }).catch(function (error) {
        try {
          pc.close();
        } catch (error2) {}

        throw error;
      });
    }
  }, {
    key: "tag",
    get: function get() {
      return 'Chrome69';
    }
  }]);

  function Chrome69(direction, extendedRtpCapabilities, settings) {
    _classCallCheck(this, Chrome69);

    logger.debug('constructor() [direction:%s, extendedRtpCapabilities:%o]', direction, extendedRtpCapabilities);
    var rtpParametersByKind;

    switch (direction) {
      case 'send':
        {
          rtpParametersByKind = {
            audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
          };
          return new SendHandler(rtpParametersByKind, settings);
        }

      case 'recv':
        {
          rtpParametersByKind = {
            audio: ortc.getReceivingFullRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getReceivingFullRtpParameters('video', extendedRtpCapabilities)
          };
          return new RecvHandler(rtpParametersByKind, settings);
        }
    }
  }

  return Chrome69;
}();

exports.default = Chrome69;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/Chrome70.js":
/*!********************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/Chrome70.js ***!
  \********************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _sdpTransform = _interopRequireDefault(__webpack_require__(/*! sdp-transform */ "./node_modules/sdp-transform/lib/index.js"));

var _Logger = _interopRequireDefault(__webpack_require__(/*! ../Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter2 = _interopRequireDefault(__webpack_require__(/*! ../EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

var utils = _interopRequireWildcard(__webpack_require__(/*! ../utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

var ortc = _interopRequireWildcard(__webpack_require__(/*! ../ortc */ "./node_modules/mediasoup-client/lib-es5/ortc.js"));

var sdpCommonUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/commonUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/commonUtils.js"));

var sdpUnifiedPlanUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/unifiedPlanUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/unifiedPlanUtils.js"));

var _RemoteUnifiedPlanSdp = _interopRequireDefault(__webpack_require__(/*! ./sdp/RemoteUnifiedPlanSdp */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemoteUnifiedPlanSdp.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var logger = new _Logger.default('Chrome70');

var Handler =
/*#__PURE__*/
function (_EnhancedEventEmitter) {
  _inherits(Handler, _EnhancedEventEmitter);

  function Handler(direction, rtpParametersByKind, settings) {
    var _this;

    _classCallCheck(this, Handler);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(Handler).call(this, logger)); // RTCPeerConnection instance.
    // @type {RTCPeerConnection}

    _this._pc = new RTCPeerConnection({
      iceServers: settings.turnServers || [],
      iceTransportPolicy: settings.iceTransportPolicy,
      bundlePolicy: 'max-bundle',
      rtcpMuxPolicy: 'require',
      sdpSemantics: 'unified-plan'
    }); // Generic sending RTP parameters for audio and video.
    // @type {Object}

    _this._rtpParametersByKind = rtpParametersByKind; // Remote SDP handler.
    // @type {RemoteUnifiedPlanSdp}

    _this._remoteSdp = new _RemoteUnifiedPlanSdp.default(direction, rtpParametersByKind); // Handle RTCPeerConnection connection status.

    _this._pc.addEventListener('iceconnectionstatechange', function () {
      switch (_this._pc.iceConnectionState) {
        case 'checking':
          _this.emit('@connectionstatechange', 'connecting');

          break;

        case 'connected':
        case 'completed':
          _this.emit('@connectionstatechange', 'connected');

          break;

        case 'failed':
          _this.emit('@connectionstatechange', 'failed');

          break;

        case 'disconnected':
          _this.emit('@connectionstatechange', 'disconnected');

          break;

        case 'closed':
          _this.emit('@connectionstatechange', 'closed');

          break;
      }
    });

    return _this;
  }

  _createClass(Handler, [{
    key: "close",
    value: function close() {
      logger.debug('close()'); // Close RTCPeerConnection.

      try {
        this._pc.close();
      } catch (error) {}
    }
  }, {
    key: "remoteClosed",
    value: function remoteClosed() {
      logger.debug('remoteClosed()');
      this._transportReady = false;
      if (this._transportUpdated) this._transportUpdated = false;
    }
  }]);

  return Handler;
}(_EnhancedEventEmitter2.default);

var SendHandler =
/*#__PURE__*/
function (_Handler) {
  _inherits(SendHandler, _Handler);

  function SendHandler(rtpParametersByKind, settings) {
    var _this2;

    _classCallCheck(this, SendHandler);

    _this2 = _possibleConstructorReturn(this, _getPrototypeOf(SendHandler).call(this, 'send', rtpParametersByKind, settings)); // Got transport local and remote parameters.
    // @type {Boolean}

    _this2._transportReady = false; // Ids of alive local tracks.
    // @type {Set<Number>}

    _this2._trackIds = new Set();
    return _this2;
  }

  _createClass(SendHandler, [{
    key: "addProducer",
    value: function addProducer(producer) {
      var _this3 = this;

      var track = producer.track;
      logger.debug('addProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      if (this._trackIds.has(track.id)) return Promise.reject(new Error('track already added'));
      var transceiver;
      var localSdpObj; // Add the track id to the Set.

      this._trackIds.add(track.id);

      return Promise.resolve().then(function () {
        // Let's check if there is any inactive transceiver for same kind and
        // reuse it if so.
        transceiver = _this3._pc.getTransceivers().find(function (t) {
          return t.receiver.track.kind === track.kind && t.direction === 'inactive';
        });

        if (transceiver) {
          logger.debug('addProducer() | reusing an inactive transceiver');
          transceiver.direction = 'sendonly';
          return transceiver.sender.replaceTrack(track);
        } else {
          transceiver = _this3._pc.addTransceiver(track, {
            direction: 'sendonly'
          });
        }
      }).then(function () {
        return _this3._pc.createOffer();
      }).then(function (offer) {
        // If simulcast is set, mangle the offer.
        if (producer.simulcast) {
          logger.debug('addProducer() | enabling simulcast');

          var sdpObject = _sdpTransform.default.parse(offer.sdp);

          sdpUnifiedPlanUtils.addPlanBSimulcast(sdpObject, track, {
            mid: transceiver.mid
          });

          var offerSdp = _sdpTransform.default.write(sdpObject);

          offer = {
            type: 'offer',
            sdp: offerSdp
          };
        }

        logger.debug('addProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this3._pc.setLocalDescription(offer);
      }).then(function () {
        if (!_this3._transportReady) return _this3._setupTransport();
      }).then(function () {
        localSdpObj = _sdpTransform.default.parse(_this3._pc.localDescription.sdp);

        var remoteSdp = _this3._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('addProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this3._pc.setRemoteDescription(answer);
      }).then(function () {
        var rtpParameters = utils.clone(_this3._rtpParametersByKind[producer.kind]);
        sdpUnifiedPlanUtils.fillRtpParametersForTrack(rtpParameters, localSdpObj, track, {
          mid: transceiver.mid,
          planBSimulcast: true
        });
        return rtpParameters;
      }).catch(function (error) {
        // Panic here. Try to undo things.
        try {
          transceiver.direction = 'inactive';
        } catch (error2) {}

        _this3._trackIds.delete(track.id);

        throw error;
      });
    }
  }, {
    key: "removeProducer",
    value: function removeProducer(producer) {
      var _this4 = this;

      var track = producer.track;
      if (!this._trackIds.has(track.id)) return Promise.reject(new Error('track not found'));
      logger.debug('removeProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      return Promise.resolve().then(function () {
        // Get the associated RTCRtpSender.
        var rtpSender = _this4._pc.getSenders().find(function (s) {
          return s.track === track;
        });

        if (!rtpSender) throw new Error('local track not found');

        _this4._pc.removeTrack(rtpSender); // Remove the track id from the Set.


        _this4._trackIds.delete(track.id);

        return _this4._pc.createOffer();
      }).then(function (offer) {
        logger.debug('removeProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this4._pc.setLocalDescription(offer);
      }).then(function () {
        var localSdpObj = _sdpTransform.default.parse(_this4._pc.localDescription.sdp);

        var remoteSdp = _this4._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('removeProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this4._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "replaceProducerTrack",
    value: function replaceProducerTrack(producer, track) {
      var _this5 = this;

      logger.debug('replaceProducerTrack() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      var oldTrack = producer.track;
      return Promise.resolve().then(function () {
        // Get the associated RTCRtpSender.
        var rtpSender = _this5._pc.getSenders().find(function (s) {
          return s.track === oldTrack;
        });

        if (!rtpSender) throw new Error('local track not found');
        return rtpSender.replaceTrack(track);
      }).then(function () {
        // Remove the old track id from the Set.
        _this5._trackIds.delete(oldTrack.id); // Add the new track id to the Set.


        _this5._trackIds.add(track.id);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this6 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        return _this6._pc.createOffer({
          iceRestart: true
        });
      }).then(function (offer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this6._pc.setLocalDescription(offer);
      }).then(function () {
        var localSdpObj = _sdpTransform.default.parse(_this6._pc.localDescription.sdp);

        var remoteSdp = _this6._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this6._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this7 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // Get our local DTLS parameters.
        var transportLocalParameters = {};
        var sdp = _this7._pc.localDescription.sdp;

        var sdpObj = _sdpTransform.default.parse(sdp);

        var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj); // Let's decide that we'll be DTLS server (because we can).

        dtlsParameters.role = 'server';
        transportLocalParameters.dtlsParameters = dtlsParameters; // Provide the remote SDP handler with transport local parameters.

        _this7._remoteSdp.setTransportLocalParameters(transportLocalParameters); // We need transport remote parameters.


        return _this7.safeEmitAsPromise('@needcreatetransport', transportLocalParameters);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this7._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this7._transportReady = true;
      });
    }
  }]);

  return SendHandler;
}(Handler);

var RecvHandler =
/*#__PURE__*/
function (_Handler2) {
  _inherits(RecvHandler, _Handler2);

  function RecvHandler(rtpParametersByKind, settings) {
    var _this8;

    _classCallCheck(this, RecvHandler);

    _this8 = _possibleConstructorReturn(this, _getPrototypeOf(RecvHandler).call(this, 'recv', rtpParametersByKind, settings)); // Got transport remote parameters.
    // @type {Boolean}

    _this8._transportCreated = false; // Got transport local parameters.
    // @type {Boolean}

    _this8._transportUpdated = false; // Map of Consumers information indexed by consumer.id.
    // - mid {String}
    // - kind {String}
    // - closed {Boolean}
    // - trackId {String}
    // - ssrc {Number}
    // - rtxSsrc {Number}
    // - cname {String}
    // @type {Map<Number, Object>}

    _this8._consumerInfos = new Map();
    return _this8;
  }

  _createClass(RecvHandler, [{
    key: "addConsumer",
    value: function addConsumer(consumer) {
      var _this9 = this;

      logger.debug('addConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      if (this._consumerInfos.has(consumer.id)) return Promise.reject(new Error('Consumer already added'));
      var encoding = consumer.rtpParameters.encodings[0];
      var cname = consumer.rtpParameters.rtcp.cname;
      var consumerInfo = {
        mid: "".concat(consumer.kind[0]).concat(consumer.id),
        kind: consumer.kind,
        closed: consumer.closed,
        streamId: "recv-stream-".concat(consumer.id),
        trackId: "consumer-".concat(consumer.kind, "-").concat(consumer.id),
        ssrc: encoding.ssrc,
        cname: cname
      };
      if (encoding.rtx && encoding.rtx.ssrc) consumerInfo.rtxSsrc = encoding.rtx.ssrc;

      this._consumerInfos.set(consumer.id, consumerInfo);

      return Promise.resolve().then(function () {
        if (!_this9._transportCreated) return _this9._setupTransport();
      }).then(function () {
        var remoteSdp = _this9._remoteSdp.createOfferSdp(Array.from(_this9._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('addConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this9._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this9._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('addConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this9._pc.setLocalDescription(answer);
      }).then(function () {
        if (!_this9._transportUpdated) return _this9._updateTransport();
      }).then(function () {
        var transceiver = _this9._pc.getTransceivers().find(function (t) {
          return t.mid === consumerInfo.mid;
        });

        if (!transceiver) throw new Error('remote track not found');
        return transceiver.receiver.track;
      });
    }
  }, {
    key: "removeConsumer",
    value: function removeConsumer(consumer) {
      var _this10 = this;

      logger.debug('removeConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);

      var consumerInfo = this._consumerInfos.get(consumer.id);

      if (!consumerInfo) return Promise.reject(new Error('Consumer not found'));
      consumerInfo.closed = true;
      return Promise.resolve().then(function () {
        var remoteSdp = _this10._remoteSdp.createOfferSdp(Array.from(_this10._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('removeConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this10._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this10._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('removeConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this10._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this11 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        var remoteSdp = _this11._remoteSdp.createOfferSdp(Array.from(_this11._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this11._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this11._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this11._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this12 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // We need transport remote parameters.
        return _this12.safeEmitAsPromise('@needcreatetransport', null);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this12._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this12._transportCreated = true;
      });
    }
  }, {
    key: "_updateTransport",
    value: function _updateTransport() {
      logger.debug('_updateTransport()'); // Get our local DTLS parameters.

      var sdp = this._pc.localDescription.sdp;

      var sdpObj = _sdpTransform.default.parse(sdp);

      var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj);
      var transportLocalParameters = {
        dtlsParameters: dtlsParameters
      }; // We need to provide transport local parameters.

      this.safeEmit('@needupdatetransport', transportLocalParameters);
      this._transportUpdated = true;
    }
  }]);

  return RecvHandler;
}(Handler);

var Chrome70 =
/*#__PURE__*/
function () {
  _createClass(Chrome70, null, [{
    key: "getNativeRtpCapabilities",
    value: function getNativeRtpCapabilities() {
      logger.debug('getNativeRtpCapabilities()');
      var pc = new RTCPeerConnection({
        iceServers: [],
        iceTransportPolicy: 'all',
        bundlePolicy: 'max-bundle',
        rtcpMuxPolicy: 'require',
        sdpSemantics: 'unified-plan'
      });
      pc.addTransceiver('audio');
      pc.addTransceiver('video');
      return pc.createOffer().then(function (offer) {
        try {
          pc.close();
        } catch (error) {}

        var sdpObj = _sdpTransform.default.parse(offer.sdp);

        var nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities(sdpObj);
        return nativeRtpCapabilities;
      }).catch(function (error) {
        try {
          pc.close();
        } catch (error2) {}

        throw error;
      });
    }
  }, {
    key: "tag",
    get: function get() {
      return 'Chrome70';
    }
  }]);

  function Chrome70(direction, extendedRtpCapabilities, settings) {
    _classCallCheck(this, Chrome70);

    logger.debug('constructor() [direction:%s, extendedRtpCapabilities:%o]', direction, extendedRtpCapabilities);
    var rtpParametersByKind;

    switch (direction) {
      case 'send':
        {
          rtpParametersByKind = {
            audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
          };
          return new SendHandler(rtpParametersByKind, settings);
        }

      case 'recv':
        {
          rtpParametersByKind = {
            audio: ortc.getReceivingFullRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getReceivingFullRtpParameters('video', extendedRtpCapabilities)
          };
          return new RecvHandler(rtpParametersByKind, settings);
        }
    }
  }

  return Chrome70;
}();

exports.default = Chrome70;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/Edge11.js":
/*!******************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/Edge11.js ***!
  \******************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _Logger = _interopRequireDefault(__webpack_require__(/*! ../Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter2 = _interopRequireDefault(__webpack_require__(/*! ../EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

var utils = _interopRequireWildcard(__webpack_require__(/*! ../utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

var ortc = _interopRequireWildcard(__webpack_require__(/*! ../ortc */ "./node_modules/mediasoup-client/lib-es5/ortc.js"));

var edgeUtils = _interopRequireWildcard(__webpack_require__(/*! ./ortc/edgeUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/ortc/edgeUtils.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var CNAME = "CNAME-EDGE-".concat(utils.randomNumber());
var logger = new _Logger.default('Edge11');

var Edge11 =
/*#__PURE__*/
function (_EnhancedEventEmitter) {
  _inherits(Edge11, _EnhancedEventEmitter);

  _createClass(Edge11, null, [{
    key: "getNativeRtpCapabilities",
    value: function getNativeRtpCapabilities() {
      logger.debug('getNativeRtpCapabilities()');
      return Promise.resolve(edgeUtils.getCapabilities());
    }
  }, {
    key: "tag",
    get: function get() {
      return 'Edge11';
    }
  }]);

  function Edge11(direction, extendedRtpCapabilities, settings) {
    var _this;

    _classCallCheck(this, Edge11);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(Edge11).call(this, logger));
    logger.debug('constructor() [direction:%s, extendedRtpCapabilities:%o]', direction, extendedRtpCapabilities); // Generic sending RTP parameters for audio and video.
    // @type {Object}

    _this._rtpParametersByKind = {
      audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
      video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
    }; // Got transport local and remote parameters.
    // @type {Boolean}

    _this._transportReady = false; // ICE gatherer.

    _this._iceGatherer = null; // ICE transport.

    _this._iceTransport = null; // DTLS transport.
    // @type {RTCDtlsTransport}

    _this._dtlsTransport = null; // Map of RTCRtpSenders indexed by Producer.id.
    // @type {Map<Number, RTCRtpSender}

    _this._rtpSenders = new Map(); // Map of RTCRtpReceivers indexed by Consumer.id.
    // @type {Map<Number, RTCRtpReceiver}

    _this._rtpReceivers = new Map(); // Remote Transport parameters.
    // @type {Object}

    _this._transportRemoteParameters = null;

    _this._setIceGatherer(settings);

    _this._setIceTransport();

    _this._setDtlsTransport();

    return _this;
  }

  _createClass(Edge11, [{
    key: "close",
    value: function close() {
      logger.debug('close()'); // Close the ICE gatherer.
      // NOTE: Not yet implemented by Edge.

      try {
        this._iceGatherer.close();
      } catch (error) {} // Close the ICE transport.


      try {
        this._iceTransport.stop();
      } catch (error) {} // Close the DTLS transport.


      try {
        this._dtlsTransport.stop();
      } catch (error) {} // Close RTCRtpSenders.


      var _iteratorNormalCompletion = true;
      var _didIteratorError = false;
      var _iteratorError = undefined;

      try {
        for (var _iterator = this._rtpSenders.values()[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
          var rtpSender = _step.value;

          try {
            rtpSender.stop();
          } catch (error) {}
        } // Close RTCRtpReceivers.

      } catch (err) {
        _didIteratorError = true;
        _iteratorError = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion && _iterator.return != null) {
            _iterator.return();
          }
        } finally {
          if (_didIteratorError) {
            throw _iteratorError;
          }
        }
      }

      var _iteratorNormalCompletion2 = true;
      var _didIteratorError2 = false;
      var _iteratorError2 = undefined;

      try {
        for (var _iterator2 = this._rtpReceivers.values()[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
          var rtpReceiver = _step2.value;

          try {
            rtpReceiver.stop();
          } catch (error) {}
        }
      } catch (err) {
        _didIteratorError2 = true;
        _iteratorError2 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion2 && _iterator2.return != null) {
            _iterator2.return();
          }
        } finally {
          if (_didIteratorError2) {
            throw _iteratorError2;
          }
        }
      }
    }
  }, {
    key: "remoteClosed",
    value: function remoteClosed() {
      logger.debug('remoteClosed()');
      this._transportReady = false;
    }
  }, {
    key: "addProducer",
    value: function addProducer(producer) {
      var _this2 = this;

      var track = producer.track;
      logger.debug('addProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      if (this._rtpSenders.has(producer.id)) return Promise.reject(new Error('Producer already added'));
      return Promise.resolve().then(function () {
        if (!_this2._transportReady) return _this2._setupTransport();
      }).then(function () {
        logger.debug('addProducer() | calling new RTCRtpSender()');
        var rtpSender = new RTCRtpSender(track, _this2._dtlsTransport);
        var rtpParameters = utils.clone(_this2._rtpParametersByKind[producer.kind]); // Fill RTCRtpParameters.encodings.

        var encoding = {
          ssrc: utils.randomNumber()
        };

        if (rtpParameters.codecs.some(function (codec) {
          return codec.name === 'rtx';
        })) {
          encoding.rtx = {
            ssrc: utils.randomNumber()
          };
        }

        rtpParameters.encodings.push(encoding); // Fill RTCRtpParameters.rtcp.

        rtpParameters.rtcp = {
          cname: CNAME,
          reducedSize: true,
          mux: true
        }; // NOTE: Convert our standard RTCRtpParameters into those that Edge
        // expects.

        var edgeRtpParameters = edgeUtils.mangleRtpParameters(rtpParameters);
        logger.debug('addProducer() | calling rtpSender.send() [params:%o]', edgeRtpParameters);
        rtpSender.send(edgeRtpParameters); // Store it.

        _this2._rtpSenders.set(producer.id, rtpSender);

        return rtpParameters;
      });
    }
  }, {
    key: "removeProducer",
    value: function removeProducer(producer) {
      var _this3 = this;

      var track = producer.track;
      logger.debug('removeProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      return Promise.resolve().then(function () {
        var rtpSender = _this3._rtpSenders.get(producer.id);

        if (!rtpSender) throw new Error('RTCRtpSender not found');

        _this3._rtpSenders.delete(producer.id);

        try {
          logger.debug('removeProducer() | calling rtpSender.stop()');
          rtpSender.stop();
        } catch (error) {
          logger.warn('rtpSender.stop() failed:%o', error);
        }
      });
    }
  }, {
    key: "replaceProducerTrack",
    value: function replaceProducerTrack(producer, track) {
      var _this4 = this;

      logger.debug('replaceProducerTrack() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      return Promise.resolve().then(function () {
        var rtpSender = _this4._rtpSenders.get(producer.id);

        if (!rtpSender) throw new Error('RTCRtpSender not found');
        rtpSender.setTrack(track);
      });
    }
  }, {
    key: "addConsumer",
    value: function addConsumer(consumer) {
      var _this5 = this;

      logger.debug('addConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      if (this._rtpReceivers.has(consumer.id)) return Promise.reject(new Error('Consumer already added'));
      return Promise.resolve().then(function () {
        if (!_this5._transportReady) return _this5._setupTransport();
      }).then(function () {
        logger.debug('addConsumer() | calling new RTCRtpReceiver()');
        var rtpReceiver = new RTCRtpReceiver(_this5._dtlsTransport, consumer.kind);
        rtpReceiver.addEventListener('error', function (event) {
          logger.error('iceGatherer "error" event [event:%o]', event);
        }); // NOTE: Convert our standard RTCRtpParameters into those that Edge
        // expects.

        var edgeRtpParameters = edgeUtils.mangleRtpParameters(consumer.rtpParameters); // Ignore MID RTP extension for receiving media.

        edgeRtpParameters.headerExtensions = edgeRtpParameters.headerExtensions.filter(function (extension) {
          return extension.uri !== 'urn:ietf:params:rtp-hdrext:sdes:mid';
        });
        logger.debug('addConsumer() | calling rtpReceiver.receive() [params:%o]', edgeRtpParameters);
        rtpReceiver.receive(edgeRtpParameters); // Store it.

        _this5._rtpReceivers.set(consumer.id, rtpReceiver);

        return rtpReceiver.track;
      });
    }
  }, {
    key: "removeConsumer",
    value: function removeConsumer(consumer) {
      var _this6 = this;

      logger.debug('removeConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      return Promise.resolve().then(function () {
        var rtpReceiver = _this6._rtpReceivers.get(consumer.id);

        if (!rtpReceiver) throw new Error('RTCRtpReceiver not found');

        _this6._rtpReceivers.delete(consumer.id);

        try {
          logger.debug('removeConsumer() | calling rtpReceiver.stop()');
          rtpReceiver.stop();
        } catch (error) {
          logger.warn('rtpReceiver.stop() failed:%o', error);
        }
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this7 = this;

      logger.debug('restartIce()');
      Promise.resolve().then(function () {
        _this7._transportRemoteParameters.iceParameters = remoteIceParameters;
        var remoteIceCandidates = _this7._transportRemoteParameters.iceCandidates;
        logger.debug('restartIce() | calling iceTransport.start()');

        _this7._iceTransport.start(_this7._iceGatherer, remoteIceParameters, 'controlling');

        var _iteratorNormalCompletion3 = true;
        var _didIteratorError3 = false;
        var _iteratorError3 = undefined;

        try {
          for (var _iterator3 = remoteIceCandidates[Symbol.iterator](), _step3; !(_iteratorNormalCompletion3 = (_step3 = _iterator3.next()).done); _iteratorNormalCompletion3 = true) {
            var candidate = _step3.value;

            _this7._iceTransport.addRemoteCandidate(candidate);
          }
        } catch (err) {
          _didIteratorError3 = true;
          _iteratorError3 = err;
        } finally {
          try {
            if (!_iteratorNormalCompletion3 && _iterator3.return != null) {
              _iterator3.return();
            }
          } finally {
            if (_didIteratorError3) {
              throw _iteratorError3;
            }
          }
        }

        _this7._iceTransport.addRemoteCandidate({});
      });
    }
  }, {
    key: "_setIceGatherer",
    value: function _setIceGatherer(settings) {
      var iceGatherer = new RTCIceGatherer({
        iceServers: settings.turnServers || [],
        gatherPolicy: settings.iceTransportPolicy
      });
      iceGatherer.addEventListener('error', function (event) {
        logger.error('iceGatherer "error" event [event:%o]', event);
      }); // NOTE: Not yet implemented by Edge, which starts gathering automatically.

      try {
        iceGatherer.gather();
      } catch (error) {
        logger.debug('iceGatherer.gather() failed: %s', error.toString());
      }

      this._iceGatherer = iceGatherer;
    }
  }, {
    key: "_setIceTransport",
    value: function _setIceTransport() {
      var _this8 = this;

      var iceTransport = new RTCIceTransport(this._iceGatherer); // NOTE: Not yet implemented by Edge.

      iceTransport.addEventListener('statechange', function () {
        switch (iceTransport.state) {
          case 'checking':
            _this8.emit('@connectionstatechange', 'connecting');

            break;

          case 'connected':
          case 'completed':
            _this8.emit('@connectionstatechange', 'connected');

            break;

          case 'failed':
            _this8.emit('@connectionstatechange', 'failed');

            break;

          case 'disconnected':
            _this8.emit('@connectionstatechange', 'disconnected');

            break;

          case 'closed':
            _this8.emit('@connectionstatechange', 'closed');

            break;
        }
      }); // NOTE: Not standard, but implemented by Edge.

      iceTransport.addEventListener('icestatechange', function () {
        switch (iceTransport.state) {
          case 'checking':
            _this8.emit('@connectionstatechange', 'connecting');

            break;

          case 'connected':
          case 'completed':
            _this8.emit('@connectionstatechange', 'connected');

            break;

          case 'failed':
            _this8.emit('@connectionstatechange', 'failed');

            break;

          case 'disconnected':
            _this8.emit('@connectionstatechange', 'disconnected');

            break;

          case 'closed':
            _this8.emit('@connectionstatechange', 'closed');

            break;
        }
      });
      iceTransport.addEventListener('candidatepairchange', function (event) {
        logger.debug('iceTransport "candidatepairchange" event [pair:%o]', event.pair);
      });
      this._iceTransport = iceTransport;
    }
  }, {
    key: "_setDtlsTransport",
    value: function _setDtlsTransport() {
      var _this9 = this;

      var dtlsTransport = new RTCDtlsTransport(this._iceTransport); // NOTE: Not yet implemented by Edge.

      dtlsTransport.addEventListener('statechange', function () {
        logger.debug('dtlsTransport "statechange" event [state:%s]', dtlsTransport.state);
      }); // NOTE: Not standard, but implemented by Edge.

      dtlsTransport.addEventListener('dtlsstatechange', function () {
        logger.debug('dtlsTransport "dtlsstatechange" event [state:%s]', dtlsTransport.state);
        if (dtlsTransport.state === 'closed') _this9.emit('@connectionstatechange', 'closed');
      });
      dtlsTransport.addEventListener('error', function (event) {
        logger.error('dtlsTransport "error" event [event:%o]', event);
      });
      this._dtlsTransport = dtlsTransport;
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this10 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // Get our local DTLS parameters.
        var transportLocalParameters = {};

        var dtlsParameters = _this10._dtlsTransport.getLocalParameters(); // Let's decide that we'll be DTLS server (because we can).


        dtlsParameters.role = 'server';
        transportLocalParameters.dtlsParameters = dtlsParameters; // We need transport remote parameters.

        return _this10.safeEmitAsPromise('@needcreatetransport', transportLocalParameters);
      }).then(function (transportRemoteParameters) {
        _this10._transportRemoteParameters = transportRemoteParameters;
        var remoteIceParameters = transportRemoteParameters.iceParameters;
        var remoteIceCandidates = transportRemoteParameters.iceCandidates;
        var remoteDtlsParameters = transportRemoteParameters.dtlsParameters; // Start the RTCIceTransport.

        _this10._iceTransport.start(_this10._iceGatherer, remoteIceParameters, 'controlling'); // Add remote ICE candidates.


        var _iteratorNormalCompletion4 = true;
        var _didIteratorError4 = false;
        var _iteratorError4 = undefined;

        try {
          for (var _iterator4 = remoteIceCandidates[Symbol.iterator](), _step4; !(_iteratorNormalCompletion4 = (_step4 = _iterator4.next()).done); _iteratorNormalCompletion4 = true) {
            var candidate = _step4.value;

            _this10._iceTransport.addRemoteCandidate(candidate);
          } // Also signal a 'complete' candidate as per spec.
          // NOTE: It should be {complete: true} but Edge prefers {}.
          // NOTE: If we don't signal end of candidates, the Edge RTCIceTransport
          // won't enter the 'completed' state.

        } catch (err) {
          _didIteratorError4 = true;
          _iteratorError4 = err;
        } finally {
          try {
            if (!_iteratorNormalCompletion4 && _iterator4.return != null) {
              _iterator4.return();
            }
          } finally {
            if (_didIteratorError4) {
              throw _iteratorError4;
            }
          }
        }

        _this10._iceTransport.addRemoteCandidate({}); // NOTE: Edge does not like SHA less than 256.


        remoteDtlsParameters.fingerprints = remoteDtlsParameters.fingerprints.filter(function (fingerprint) {
          return fingerprint.algorithm === 'sha-256' || fingerprint.algorithm === 'sha-384' || fingerprint.algorithm === 'sha-512';
        }); // Start the RTCDtlsTransport.

        _this10._dtlsTransport.start(remoteDtlsParameters);

        _this10._transportReady = true;
      });
    }
  }]);

  return Edge11;
}(_EnhancedEventEmitter2.default);

exports.default = Edge11;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/Firefox50.js":
/*!*********************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/Firefox50.js ***!
  \*********************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _sdpTransform = _interopRequireDefault(__webpack_require__(/*! sdp-transform */ "./node_modules/sdp-transform/lib/index.js"));

var _Logger = _interopRequireDefault(__webpack_require__(/*! ../Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter2 = _interopRequireDefault(__webpack_require__(/*! ../EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

var utils = _interopRequireWildcard(__webpack_require__(/*! ../utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

var ortc = _interopRequireWildcard(__webpack_require__(/*! ../ortc */ "./node_modules/mediasoup-client/lib-es5/ortc.js"));

var sdpCommonUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/commonUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/commonUtils.js"));

var sdpUnifiedPlanUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/unifiedPlanUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/unifiedPlanUtils.js"));

var _RemoteUnifiedPlanSdp = _interopRequireDefault(__webpack_require__(/*! ./sdp/RemoteUnifiedPlanSdp */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemoteUnifiedPlanSdp.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; var ownKeys = Object.keys(source); if (typeof Object.getOwnPropertySymbols === 'function') { ownKeys = ownKeys.concat(Object.getOwnPropertySymbols(source).filter(function (sym) { return Object.getOwnPropertyDescriptor(source, sym).enumerable; })); } ownKeys.forEach(function (key) { _defineProperty(target, key, source[key]); }); } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var logger = new _Logger.default('Firefox50');

var Handler =
/*#__PURE__*/
function (_EnhancedEventEmitter) {
  _inherits(Handler, _EnhancedEventEmitter);

  function Handler(direction, rtpParametersByKind, settings) {
    var _this;

    _classCallCheck(this, Handler);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(Handler).call(this, logger)); // RTCPeerConnection instance.
    // @type {RTCPeerConnection}

    _this._pc = new RTCPeerConnection({
      iceServers: settings.turnServers || [],
      iceTransportPolicy: settings.iceTransportPolicy,
      bundlePolicy: 'max-bundle',
      rtcpMuxPolicy: 'require'
    }); // Generic sending RTP parameters for audio and video.
    // @type {Object}

    _this._rtpParametersByKind = rtpParametersByKind; // Remote SDP handler.
    // @type {RemoteUnifiedPlanSdp}

    _this._remoteSdp = new _RemoteUnifiedPlanSdp.default(direction, rtpParametersByKind); // Handle RTCPeerConnection connection status.

    _this._pc.addEventListener('iceconnectionstatechange', function () {
      switch (_this._pc.iceConnectionState) {
        case 'checking':
          _this.emit('@connectionstatechange', 'connecting');

          break;

        case 'connected':
        case 'completed':
          _this.emit('@connectionstatechange', 'connected');

          break;

        case 'failed':
          _this.emit('@connectionstatechange', 'failed');

          break;

        case 'disconnected':
          _this.emit('@connectionstatechange', 'disconnected');

          break;

        case 'closed':
          _this.emit('@connectionstatechange', 'closed');

          break;
      }
    });

    return _this;
  }

  _createClass(Handler, [{
    key: "close",
    value: function close() {
      logger.debug('close()'); // Close RTCPeerConnection.

      try {
        this._pc.close();
      } catch (error) {}
    }
  }, {
    key: "remoteClosed",
    value: function remoteClosed() {
      logger.debug('remoteClosed()');
      this._transportReady = false;
      if (this._transportUpdated) this._transportUpdated = false;
    }
  }]);

  return Handler;
}(_EnhancedEventEmitter2.default);

var SendHandler =
/*#__PURE__*/
function (_Handler) {
  _inherits(SendHandler, _Handler);

  function SendHandler(rtpParametersByKind, settings) {
    var _this2;

    _classCallCheck(this, SendHandler);

    _this2 = _possibleConstructorReturn(this, _getPrototypeOf(SendHandler).call(this, 'send', rtpParametersByKind, settings)); // Got transport local and remote parameters.
    // @type {Boolean}

    _this2._transportReady = false; // Local stream.
    // @type {MediaStream}

    _this2._stream = new MediaStream(); // RID value counter for simulcast (so they never match).
    // @type {Number}

    _this2._nextRid = 1;
    return _this2;
  }

  _createClass(SendHandler, [{
    key: "addProducer",
    value: function addProducer(producer) {
      var _this3 = this;

      var track = producer.track;
      logger.debug('addProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      if (this._stream.getTrackById(track.id)) return Promise.reject(new Error('track already added'));
      var rtpSender;
      var localSdpObj;
      return Promise.resolve().then(function () {
        _this3._stream.addTrack(track); // Add the stream to the PeerConnection.


        rtpSender = _this3._pc.addTrack(track, _this3._stream);
      }).then(function () {
        var encodings = [];

        if (producer.simulcast) {
          logger.debug('addProducer() | enabling simulcast');

          if (producer.simulcast.high) {
            encodings.push({
              rid: "high".concat(_this3._nextRid),
              active: true,
              priority: 'low',
              maxBitrate: producer.simulcast.high
            });
          }

          if (producer.simulcast.medium) {
            encodings.push({
              rid: "medium".concat(_this3._nextRid),
              active: true,
              priority: 'medium',
              maxBitrate: producer.simulcast.medium
            });
          }

          if (producer.simulcast.low) {
            encodings.push({
              rid: "low".concat(_this3._nextRid),
              active: true,
              priority: 'high',
              maxBitrate: producer.simulcast.low
            });
          } // Update RID counter for future ones.


          _this3._nextRid++;
        }

        var parameters = rtpSender.getParameters();
        return rtpSender.setParameters(_objectSpread({}, parameters, {
          encodings: encodings
        }));
      }).then(function () {
        return _this3._pc.createOffer();
      }).then(function (offer) {
        logger.debug('addProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this3._pc.setLocalDescription(offer);
      }).then(function () {
        if (!_this3._transportReady) return _this3._setupTransport();
      }).then(function () {
        localSdpObj = _sdpTransform.default.parse(_this3._pc.localDescription.sdp);

        var remoteSdp = _this3._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('addProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this3._pc.setRemoteDescription(answer);
      }).then(function () {
        var rtpParameters = utils.clone(_this3._rtpParametersByKind[producer.kind]); // Fill the RTP parameters for this track.

        sdpUnifiedPlanUtils.fillRtpParametersForTrack(rtpParameters, localSdpObj, track);
        return rtpParameters;
      }).catch(function (error) {
        // Panic here. Try to undo things.
        try {
          _this3._pc.removeTrack(rtpSender);
        } catch (error2) {}

        _this3._stream.removeTrack(track);

        throw error;
      });
    }
  }, {
    key: "removeProducer",
    value: function removeProducer(producer) {
      var _this4 = this;

      var track = producer.track;
      logger.debug('removeProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      return Promise.resolve().then(function () {
        // Get the associated RTCRtpSender.
        var rtpSender = _this4._pc.getSenders().find(function (s) {
          return s.track === track;
        });

        if (!rtpSender) throw new Error('RTCRtpSender not found'); // Remove the associated RtpSender.

        _this4._pc.removeTrack(rtpSender); // Remove the track from the local stream.


        _this4._stream.removeTrack(track);

        return _this4._pc.createOffer();
      }).then(function (offer) {
        logger.debug('removeProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this4._pc.setLocalDescription(offer);
      }).then(function () {
        var localSdpObj = _sdpTransform.default.parse(_this4._pc.localDescription.sdp);

        var remoteSdp = _this4._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('removeProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this4._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "replaceProducerTrack",
    value: function replaceProducerTrack(producer, track) {
      var _this5 = this;

      logger.debug('replaceProducerTrack() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      var oldTrack = producer.track;
      return Promise.resolve().then(function () {
        // Get the associated RTCRtpSender.
        var rtpSender = _this5._pc.getSenders().find(function (s) {
          return s.track === oldTrack;
        });

        if (!rtpSender) throw new Error('local track not found');
        return rtpSender.replaceTrack(track);
      }).then(function () {
        // Remove the old track from the local stream.
        _this5._stream.removeTrack(oldTrack); // Add the new track to the local stream.


        _this5._stream.addTrack(track);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this6 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        return _this6._pc.createOffer({
          iceRestart: true
        });
      }).then(function (offer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this6._pc.setLocalDescription(offer);
      }).then(function () {
        var localSdpObj = _sdpTransform.default.parse(_this6._pc.localDescription.sdp);

        var remoteSdp = _this6._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this6._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this7 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // Get our local DTLS parameters.
        var transportLocalParameters = {};
        var sdp = _this7._pc.localDescription.sdp;

        var sdpObj = _sdpTransform.default.parse(sdp);

        var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj); // Let's decide that we'll be DTLS server (because we can).

        dtlsParameters.role = 'server';
        transportLocalParameters.dtlsParameters = dtlsParameters; // Provide the remote SDP handler with transport local parameters.

        _this7._remoteSdp.setTransportLocalParameters(transportLocalParameters); // We need transport remote parameters.


        return _this7.safeEmitAsPromise('@needcreatetransport', transportLocalParameters);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this7._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this7._transportReady = true;
      });
    }
  }]);

  return SendHandler;
}(Handler);

var RecvHandler =
/*#__PURE__*/
function (_Handler2) {
  _inherits(RecvHandler, _Handler2);

  function RecvHandler(rtpParametersByKind, settings) {
    var _this8;

    _classCallCheck(this, RecvHandler);

    _this8 = _possibleConstructorReturn(this, _getPrototypeOf(RecvHandler).call(this, 'recv', rtpParametersByKind, settings)); // Got transport remote parameters.
    // @type {Boolean}

    _this8._transportCreated = false; // Got transport local parameters.
    // @type {Boolean}

    _this8._transportUpdated = false; // Map of Consumers information indexed by consumer.id.
    // - mid {String}
    // - kind {String}
    // - closed {Boolean}
    // - trackId {String}
    // - ssrc {Number}
    // - rtxSsrc {Number}
    // - cname {String}
    // @type {Map<Number, Object>}

    _this8._consumerInfos = new Map(); // Add an entry into consumers info to hold a fake DataChannel, so
    // the first m= section of the remote SDP is always "active" and Firefox
    // does not close the transport when there is no remote audio/video Consumers.
    //
    // ISSUE: https://github.com/versatica/mediasoup-client/issues/2

    var fakeDataChannelConsumerInfo = {
      mid: 'fake-dc',
      kind: 'application',
      closed: false,
      cname: null
    };

    _this8._consumerInfos.set(555, fakeDataChannelConsumerInfo);

    return _this8;
  }

  _createClass(RecvHandler, [{
    key: "addConsumer",
    value: function addConsumer(consumer) {
      var _this9 = this;

      logger.debug('addConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      if (this._consumerInfos.has(consumer.id)) return Promise.reject(new Error('Consumer already added'));
      var encoding = consumer.rtpParameters.encodings[0];
      var cname = consumer.rtpParameters.rtcp.cname;
      var consumerInfo = {
        mid: "".concat(consumer.kind[0]).concat(consumer.id),
        kind: consumer.kind,
        closed: consumer.closed,
        streamId: "recv-stream-".concat(consumer.id),
        trackId: "consumer-".concat(consumer.kind, "-").concat(consumer.id),
        ssrc: encoding.ssrc,
        cname: cname
      };
      if (encoding.rtx && encoding.rtx.ssrc) consumerInfo.rtxSsrc = encoding.rtx.ssrc;

      this._consumerInfos.set(consumer.id, consumerInfo);

      return Promise.resolve().then(function () {
        if (!_this9._transportCreated) return _this9._setupTransport();
      }).then(function () {
        var remoteSdp = _this9._remoteSdp.createOfferSdp(Array.from(_this9._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('addConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this9._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this9._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('addConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this9._pc.setLocalDescription(answer);
      }).then(function () {
        if (!_this9._transportUpdated) return _this9._updateTransport();
      }).then(function () {
        var newRtpReceiver = _this9._pc.getReceivers().find(function (rtpReceiver) {
          var track = rtpReceiver.track;
          if (!track) return false;
          return track.id === consumerInfo.trackId;
        });

        if (!newRtpReceiver) throw new Error('remote track not found');
        return newRtpReceiver.track;
      });
    }
  }, {
    key: "removeConsumer",
    value: function removeConsumer(consumer) {
      var _this10 = this;

      logger.debug('removeConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);

      var consumerInfo = this._consumerInfos.get(consumer.id);

      if (!consumerInfo) return Promise.reject(new Error('Consumer not found'));
      consumerInfo.closed = true;
      return Promise.resolve().then(function () {
        var remoteSdp = _this10._remoteSdp.createOfferSdp(Array.from(_this10._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('removeConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this10._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this10._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('removeConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this10._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this11 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        var remoteSdp = _this11._remoteSdp.createOfferSdp(Array.from(_this11._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this11._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this11._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this11._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this12 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // We need transport remote parameters.
        return _this12.safeEmitAsPromise('@needcreatetransport', null);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this12._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this12._transportCreated = true;
      });
    }
  }, {
    key: "_updateTransport",
    value: function _updateTransport() {
      logger.debug('_updateTransport()'); // Get our local DTLS parameters.

      var sdp = this._pc.localDescription.sdp;

      var sdpObj = _sdpTransform.default.parse(sdp);

      var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj);
      var transportLocalParameters = {
        dtlsParameters: dtlsParameters
      }; // We need to provide transport local parameters.

      this.safeEmit('@needupdatetransport', transportLocalParameters);
      this._transportUpdated = true;
    }
  }]);

  return RecvHandler;
}(Handler);

var Firefox50 =
/*#__PURE__*/
function () {
  _createClass(Firefox50, null, [{
    key: "getNativeRtpCapabilities",
    value: function getNativeRtpCapabilities() {
      logger.debug('getNativeRtpCapabilities()');
      var pc = new RTCPeerConnection({
        iceServers: [],
        iceTransportPolicy: 'all',
        bundlePolicy: 'max-bundle',
        rtcpMuxPolicy: 'require'
      }); // NOTE: We need to add a real video track to get the RID extension mapping.

      var canvas = document.createElement('canvas'); // NOTE: Otherwise Firefox fails in next line.

      canvas.getContext('2d');
      var fakeStream = canvas.captureStream();
      var fakeVideoTrack = fakeStream.getVideoTracks()[0];
      var rtpSender = pc.addTrack(fakeVideoTrack, fakeStream);
      rtpSender.setParameters({
        encodings: [{
          rid: 'RID1',
          maxBitrate: 40000
        }, {
          rid: 'RID2',
          maxBitrate: 10000
        }]
      });
      return pc.createOffer({
        offerToReceiveAudio: true,
        offerToReceiveVideo: true
      }).then(function (offer) {
        try {
          canvas.remove();
        } catch (error) {}

        try {
          fakeVideoTrack.stop();
        } catch (error) {}

        try {
          pc.close();
        } catch (error) {}

        var sdpObj = _sdpTransform.default.parse(offer.sdp);

        var nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities(sdpObj);
        return nativeRtpCapabilities;
      }).catch(function (error) {
        try {
          canvas.remove();
        } catch (error2) {}

        try {
          fakeVideoTrack.stop();
        } catch (error2) {}

        try {
          pc.close();
        } catch (error2) {}

        throw error;
      });
    }
  }, {
    key: "tag",
    get: function get() {
      return 'Firefox50';
    }
  }]);

  function Firefox50(direction, extendedRtpCapabilities, settings) {
    _classCallCheck(this, Firefox50);

    logger.debug('constructor() [direction:%s, extendedRtpCapabilities:%o]', direction, extendedRtpCapabilities);
    var rtpParametersByKind;

    switch (direction) {
      case 'send':
        {
          rtpParametersByKind = {
            audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
          };
          return new SendHandler(rtpParametersByKind, settings);
        }

      case 'recv':
        {
          rtpParametersByKind = {
            audio: ortc.getReceivingFullRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getReceivingFullRtpParameters('video', extendedRtpCapabilities)
          };
          return new RecvHandler(rtpParametersByKind, settings);
        }
    }
  }

  return Firefox50;
}();

exports.default = Firefox50;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/Firefox59.js":
/*!*********************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/Firefox59.js ***!
  \*********************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _sdpTransform = _interopRequireDefault(__webpack_require__(/*! sdp-transform */ "./node_modules/sdp-transform/lib/index.js"));

var _Logger = _interopRequireDefault(__webpack_require__(/*! ../Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter2 = _interopRequireDefault(__webpack_require__(/*! ../EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

var utils = _interopRequireWildcard(__webpack_require__(/*! ../utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

var ortc = _interopRequireWildcard(__webpack_require__(/*! ../ortc */ "./node_modules/mediasoup-client/lib-es5/ortc.js"));

var sdpCommonUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/commonUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/commonUtils.js"));

var sdpUnifiedPlanUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/unifiedPlanUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/unifiedPlanUtils.js"));

var _RemoteUnifiedPlanSdp = _interopRequireDefault(__webpack_require__(/*! ./sdp/RemoteUnifiedPlanSdp */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemoteUnifiedPlanSdp.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; var ownKeys = Object.keys(source); if (typeof Object.getOwnPropertySymbols === 'function') { ownKeys = ownKeys.concat(Object.getOwnPropertySymbols(source).filter(function (sym) { return Object.getOwnPropertyDescriptor(source, sym).enumerable; })); } ownKeys.forEach(function (key) { _defineProperty(target, key, source[key]); }); } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var logger = new _Logger.default('Firefox59');

var Handler =
/*#__PURE__*/
function (_EnhancedEventEmitter) {
  _inherits(Handler, _EnhancedEventEmitter);

  function Handler(direction, rtpParametersByKind, settings) {
    var _this;

    _classCallCheck(this, Handler);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(Handler).call(this, logger)); // RTCPeerConnection instance.
    // @type {RTCPeerConnection}

    _this._pc = new RTCPeerConnection({
      iceServers: settings.turnServers || [],
      iceTransportPolicy: settings.iceTransportPolicy,
      bundlePolicy: 'max-bundle',
      rtcpMuxPolicy: 'require'
    }); // Generic sending RTP parameters for audio and video.
    // @type {Object}

    _this._rtpParametersByKind = rtpParametersByKind; // Remote SDP handler.
    // @type {RemoteUnifiedPlanSdp}

    _this._remoteSdp = new _RemoteUnifiedPlanSdp.default(direction, rtpParametersByKind); // Handle RTCPeerConnection connection status.

    _this._pc.addEventListener('iceconnectionstatechange', function () {
      switch (_this._pc.iceConnectionState) {
        case 'checking':
          _this.emit('@connectionstatechange', 'connecting');

          break;

        case 'connected':
        case 'completed':
          _this.emit('@connectionstatechange', 'connected');

          break;

        case 'failed':
          _this.emit('@connectionstatechange', 'failed');

          break;

        case 'disconnected':
          _this.emit('@connectionstatechange', 'disconnected');

          break;

        case 'closed':
          _this.emit('@connectionstatechange', 'closed');

          break;
      }
    });

    return _this;
  }

  _createClass(Handler, [{
    key: "close",
    value: function close() {
      logger.debug('close()'); // Close RTCPeerConnection.

      try {
        this._pc.close();
      } catch (error) {}
    }
  }, {
    key: "remoteClosed",
    value: function remoteClosed() {
      logger.debug('remoteClosed()');
      this._transportReady = false;
      if (this._transportUpdated) this._transportUpdated = false;
    }
  }]);

  return Handler;
}(_EnhancedEventEmitter2.default);

var SendHandler =
/*#__PURE__*/
function (_Handler) {
  _inherits(SendHandler, _Handler);

  function SendHandler(rtpParametersByKind, settings) {
    var _this2;

    _classCallCheck(this, SendHandler);

    _this2 = _possibleConstructorReturn(this, _getPrototypeOf(SendHandler).call(this, 'send', rtpParametersByKind, settings)); // Got transport local and remote parameters.
    // @type {Boolean}

    _this2._transportReady = false; // Local stream.
    // @type {MediaStream}

    _this2._stream = new MediaStream(); // RID value counter for simulcast (so they never match).
    // @type {Number}

    _this2._nextRid = 1;
    return _this2;
  }

  _createClass(SendHandler, [{
    key: "addProducer",
    value: function addProducer(producer) {
      var _this3 = this;

      var track = producer.track;
      logger.debug('addProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      if (this._stream.getTrackById(track.id)) return Promise.reject(new Error('track already added'));
      var rtpSender;
      var localSdpObj;
      return Promise.resolve().then(function () {
        // Add the track to the local stream.
        _this3._stream.addTrack(track); // Add the stream to the PeerConnection.


        rtpSender = _this3._pc.addTrack(track, _this3._stream);
      }).then(function () {
        var encodings = [];

        if (producer.simulcast) {
          logger.debug('addProducer() | enabling simulcast');

          if (producer.simulcast.high) {
            encodings.push({
              rid: "high".concat(_this3._nextRid),
              active: true,
              priority: 'low',
              maxBitrate: producer.simulcast.high
            });
          }

          if (producer.simulcast.medium) {
            encodings.push({
              rid: "medium".concat(_this3._nextRid),
              active: true,
              priority: 'medium',
              maxBitrate: producer.simulcast.medium
            });
          }

          if (producer.simulcast.low) {
            encodings.push({
              rid: "low".concat(_this3._nextRid),
              active: true,
              priority: 'high',
              maxBitrate: producer.simulcast.low
            });
          } // Update RID counter for future ones.


          _this3._nextRid++;
        }

        var parameters = rtpSender.getParameters();
        return rtpSender.setParameters(_objectSpread({}, parameters, {
          encodings: encodings
        }));
      }).then(function () {
        return _this3._pc.createOffer();
      }).then(function (offer) {
        logger.debug('addProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this3._pc.setLocalDescription(offer);
      }).then(function () {
        if (!_this3._transportReady) return _this3._setupTransport();
      }).then(function () {
        localSdpObj = _sdpTransform.default.parse(_this3._pc.localDescription.sdp);

        var remoteSdp = _this3._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('addProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this3._pc.setRemoteDescription(answer);
      }).then(function () {
        var rtpParameters = utils.clone(_this3._rtpParametersByKind[producer.kind]); // Fill the RTP parameters for this track.

        sdpUnifiedPlanUtils.fillRtpParametersForTrack(rtpParameters, localSdpObj, track);
        return rtpParameters;
      }).catch(function (error) {
        // Panic here. Try to undo things.
        try {
          _this3._pc.removeTrack(rtpSender);
        } catch (error2) {}

        _this3._stream.removeTrack(track);

        throw error;
      });
    }
  }, {
    key: "removeProducer",
    value: function removeProducer(producer) {
      var _this4 = this;

      var track = producer.track;
      logger.debug('removeProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      return Promise.resolve().then(function () {
        // Get the associated RTCRtpSender.
        var rtpSender = _this4._pc.getSenders().find(function (s) {
          return s.track === track;
        });

        if (!rtpSender) throw new Error('RTCRtpSender not found'); // Remove the associated RtpSender.

        _this4._pc.removeTrack(rtpSender); // Remove the track from the local stream.


        _this4._stream.removeTrack(track);

        return _this4._pc.createOffer();
      }).then(function (offer) {
        logger.debug('removeProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this4._pc.setLocalDescription(offer);
      }).then(function () {
        var localSdpObj = _sdpTransform.default.parse(_this4._pc.localDescription.sdp);

        var remoteSdp = _this4._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('removeProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this4._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "replaceProducerTrack",
    value: function replaceProducerTrack(producer, track) {
      var _this5 = this;

      logger.debug('replaceProducerTrack() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      var oldTrack = producer.track;
      return Promise.resolve().then(function () {
        // Get the associated RTCRtpSender.
        var rtpSender = _this5._pc.getSenders().find(function (s) {
          return s.track === oldTrack;
        });

        if (!rtpSender) throw new Error('local track not found');
        return rtpSender.replaceTrack(track);
      }).then(function () {
        // Remove the old track from the local stream.
        _this5._stream.removeTrack(oldTrack); // Add the new track to the local stream.


        _this5._stream.addTrack(track);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this6 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        return _this6._pc.createOffer({
          iceRestart: true
        });
      }).then(function (offer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this6._pc.setLocalDescription(offer);
      }).then(function () {
        var localSdpObj = _sdpTransform.default.parse(_this6._pc.localDescription.sdp);

        var remoteSdp = _this6._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this6._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this7 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // Get our local DTLS parameters.
        var transportLocalParameters = {};
        var sdp = _this7._pc.localDescription.sdp;

        var sdpObj = _sdpTransform.default.parse(sdp);

        var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj); // Let's decide that we'll be DTLS server (because we can).

        dtlsParameters.role = 'server';
        transportLocalParameters.dtlsParameters = dtlsParameters; // Provide the remote SDP handler with transport local parameters.

        _this7._remoteSdp.setTransportLocalParameters(transportLocalParameters); // We need transport remote parameters.


        return _this7.safeEmitAsPromise('@needcreatetransport', transportLocalParameters);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this7._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this7._transportReady = true;
      });
    }
  }]);

  return SendHandler;
}(Handler);

var RecvHandler =
/*#__PURE__*/
function (_Handler2) {
  _inherits(RecvHandler, _Handler2);

  function RecvHandler(rtpParametersByKind, settings) {
    var _this8;

    _classCallCheck(this, RecvHandler);

    _this8 = _possibleConstructorReturn(this, _getPrototypeOf(RecvHandler).call(this, 'recv', rtpParametersByKind, settings)); // Got transport remote parameters.
    // @type {Boolean}

    _this8._transportCreated = false; // Got transport local parameters.
    // @type {Boolean}

    _this8._transportUpdated = false; // Map of Consumers information indexed by consumer.id.
    // - mid {String}
    // - kind {String}
    // - closed {Boolean}
    // - trackId {String}
    // - ssrc {Number}
    // - rtxSsrc {Number}
    // - cname {String}
    // @type {Map<Number, Object>}

    _this8._consumerInfos = new Map();
    return _this8;
  }

  _createClass(RecvHandler, [{
    key: "addConsumer",
    value: function addConsumer(consumer) {
      var _this9 = this;

      logger.debug('addConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      if (this._consumerInfos.has(consumer.id)) return Promise.reject(new Error('Consumer already added'));
      var encoding = consumer.rtpParameters.encodings[0];
      var cname = consumer.rtpParameters.rtcp.cname;
      var consumerInfo = {
        mid: "".concat(consumer.kind[0]).concat(consumer.id),
        kind: consumer.kind,
        closed: consumer.closed,
        streamId: "recv-stream-".concat(consumer.id),
        trackId: "consumer-".concat(consumer.kind, "-").concat(consumer.id),
        ssrc: encoding.ssrc,
        cname: cname
      };
      if (encoding.rtx && encoding.rtx.ssrc) consumerInfo.rtxSsrc = encoding.rtx.ssrc;

      this._consumerInfos.set(consumer.id, consumerInfo);

      return Promise.resolve().then(function () {
        if (!_this9._transportCreated) return _this9._setupTransport();
      }).then(function () {
        var remoteSdp = _this9._remoteSdp.createOfferSdp(Array.from(_this9._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('addConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this9._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this9._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('addConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this9._pc.setLocalDescription(answer);
      }).then(function () {
        if (!_this9._transportUpdated) return _this9._updateTransport();
      }).then(function () {
        var newTransceiver = _this9._pc.getTransceivers().find(function (transceiver) {
          var receiver = transceiver.receiver;
          if (!receiver) return false;
          var track = receiver.track;
          if (!track) return false;
          return transceiver.mid === consumerInfo.mid;
        });

        if (!newTransceiver) throw new Error('remote track not found');
        return newTransceiver.receiver.track;
      });
    }
  }, {
    key: "removeConsumer",
    value: function removeConsumer(consumer) {
      var _this10 = this;

      logger.debug('removeConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);

      var consumerInfo = this._consumerInfos.get(consumer.id);

      if (!consumerInfo) return Promise.reject(new Error('Consumer not found'));
      consumerInfo.closed = true;
      return Promise.resolve().then(function () {
        var remoteSdp = _this10._remoteSdp.createOfferSdp(Array.from(_this10._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('removeConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this10._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this10._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('removeConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this10._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this11 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        var remoteSdp = _this11._remoteSdp.createOfferSdp(Array.from(_this11._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this11._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this11._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this11._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this12 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // We need transport remote parameters.
        return _this12.safeEmitAsPromise('@needcreatetransport', null);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this12._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this12._transportCreated = true;
      });
    }
  }, {
    key: "_updateTransport",
    value: function _updateTransport() {
      logger.debug('_updateTransport()'); // Get our local DTLS parameters.

      var sdp = this._pc.localDescription.sdp;

      var sdpObj = _sdpTransform.default.parse(sdp);

      var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj);
      var transportLocalParameters = {
        dtlsParameters: dtlsParameters
      }; // We need to provide transport local parameters.

      this.safeEmit('@needupdatetransport', transportLocalParameters);
      this._transportUpdated = true;
    }
  }]);

  return RecvHandler;
}(Handler);

var Firefox59 =
/*#__PURE__*/
function () {
  _createClass(Firefox59, null, [{
    key: "getNativeRtpCapabilities",
    value: function getNativeRtpCapabilities() {
      logger.debug('getNativeRtpCapabilities()');
      var pc = new RTCPeerConnection({
        iceServers: [],
        iceTransportPolicy: 'all',
        bundlePolicy: 'max-bundle',
        rtcpMuxPolicy: 'require'
      }); // NOTE: We need to add a real video track to get the RID extension mapping.

      var canvas = document.createElement('canvas'); // NOTE: Otherwise Firefox fails in next line.

      canvas.getContext('2d');
      var fakeStream = canvas.captureStream();
      var fakeVideoTrack = fakeStream.getVideoTracks()[0];
      var rtpSender = pc.addTrack(fakeVideoTrack, fakeStream);
      rtpSender.setParameters({
        encodings: [{
          rid: 'RID1',
          maxBitrate: 40000
        }, {
          rid: 'RID2',
          maxBitrate: 10000
        }]
      });
      return pc.createOffer({
        offerToReceiveAudio: true,
        offerToReceiveVideo: true
      }).then(function (offer) {
        try {
          canvas.remove();
        } catch (error) {}

        try {
          fakeVideoTrack.stop();
        } catch (error) {}

        try {
          pc.close();
        } catch (error) {}

        var sdpObj = _sdpTransform.default.parse(offer.sdp);

        var nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities(sdpObj);
        return nativeRtpCapabilities;
      }).catch(function (error) {
        try {
          canvas.remove();
        } catch (error2) {}

        try {
          fakeVideoTrack.stop();
        } catch (error2) {}

        try {
          pc.close();
        } catch (error2) {}

        throw error;
      });
    }
  }, {
    key: "tag",
    get: function get() {
      return 'Firefox59';
    }
  }]);

  function Firefox59(direction, extendedRtpCapabilities, settings) {
    _classCallCheck(this, Firefox59);

    logger.debug('constructor() [direction:%s, extendedRtpCapabilities:%o]', direction, extendedRtpCapabilities);
    var rtpParametersByKind;

    switch (direction) {
      case 'send':
        {
          rtpParametersByKind = {
            audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
          };
          return new SendHandler(rtpParametersByKind, settings);
        }

      case 'recv':
        {
          rtpParametersByKind = {
            audio: ortc.getReceivingFullRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getReceivingFullRtpParameters('video', extendedRtpCapabilities)
          };
          return new RecvHandler(rtpParametersByKind, settings);
        }
    }
  }

  return Firefox59;
}();

exports.default = Firefox59;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/Firefox65.js":
/*!*********************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/Firefox65.js ***!
  \*********************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _sdpTransform = _interopRequireDefault(__webpack_require__(/*! sdp-transform */ "./node_modules/sdp-transform/lib/index.js"));

var _Logger = _interopRequireDefault(__webpack_require__(/*! ../Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter2 = _interopRequireDefault(__webpack_require__(/*! ../EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

var utils = _interopRequireWildcard(__webpack_require__(/*! ../utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

var ortc = _interopRequireWildcard(__webpack_require__(/*! ../ortc */ "./node_modules/mediasoup-client/lib-es5/ortc.js"));

var sdpCommonUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/commonUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/commonUtils.js"));

var sdpUnifiedPlanUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/unifiedPlanUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/unifiedPlanUtils.js"));

var _RemoteUnifiedPlanSdp = _interopRequireDefault(__webpack_require__(/*! ./sdp/RemoteUnifiedPlanSdp */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemoteUnifiedPlanSdp.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; var ownKeys = Object.keys(source); if (typeof Object.getOwnPropertySymbols === 'function') { ownKeys = ownKeys.concat(Object.getOwnPropertySymbols(source).filter(function (sym) { return Object.getOwnPropertyDescriptor(source, sym).enumerable; })); } ownKeys.forEach(function (key) { _defineProperty(target, key, source[key]); }); } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var logger = new _Logger.default('Firefox65');

var Handler =
/*#__PURE__*/
function (_EnhancedEventEmitter) {
  _inherits(Handler, _EnhancedEventEmitter);

  function Handler(direction, rtpParametersByKind, settings) {
    var _this;

    _classCallCheck(this, Handler);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(Handler).call(this, logger)); // RTCPeerConnection instance.
    // @type {RTCPeerConnection}

    _this._pc = new RTCPeerConnection({
      iceServers: settings.turnServers || [],
      iceTransportPolicy: settings.iceTransportPolicy,
      bundlePolicy: 'max-bundle',
      rtcpMuxPolicy: 'require'
    }); // Generic sending RTP parameters for audio and video.
    // @type {Object}

    _this._rtpParametersByKind = rtpParametersByKind; // Remote SDP handler.
    // @type {RemoteUnifiedPlanSdp}

    _this._remoteSdp = new _RemoteUnifiedPlanSdp.default(direction, rtpParametersByKind); // Handle RTCPeerConnection connection status.

    _this._pc.addEventListener('iceconnectionstatechange', function () {
      switch (_this._pc.iceConnectionState) {
        case 'checking':
          _this.emit('@connectionstatechange', 'connecting');

          break;

        case 'connected':
        case 'completed':
          _this.emit('@connectionstatechange', 'connected');

          break;

        case 'failed':
          _this.emit('@connectionstatechange', 'failed');

          break;

        case 'disconnected':
          _this.emit('@connectionstatechange', 'disconnected');

          break;

        case 'closed':
          _this.emit('@connectionstatechange', 'closed');

          break;
      }
    });

    return _this;
  }

  _createClass(Handler, [{
    key: "close",
    value: function close() {
      logger.debug('close()'); // Close RTCPeerConnection.

      try {
        this._pc.close();
      } catch (error) {}
    }
  }, {
    key: "remoteClosed",
    value: function remoteClosed() {
      logger.debug('remoteClosed()');
      this._transportReady = false;
      if (this._transportUpdated) this._transportUpdated = false;
    }
  }]);

  return Handler;
}(_EnhancedEventEmitter2.default);

var SendHandler =
/*#__PURE__*/
function (_Handler) {
  _inherits(SendHandler, _Handler);

  function SendHandler(rtpParametersByKind, settings) {
    var _this2;

    _classCallCheck(this, SendHandler);

    _this2 = _possibleConstructorReturn(this, _getPrototypeOf(SendHandler).call(this, 'send', rtpParametersByKind, settings)); // Got transport local and remote parameters.
    // @type {Boolean}

    _this2._transportReady = false; // Ids of alive local tracks.
    // @type {Set<Number>}

    _this2._trackIds = new Set(); // RID value counter for simulcast (so they never match).
    // @type {Number}

    _this2._nextRid = 1;
    return _this2;
  }

  _createClass(SendHandler, [{
    key: "addProducer",
    value: function addProducer(producer) {
      var _this3 = this;

      var track = producer.track;
      logger.debug('addProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      if (this._trackIds.has(track.id)) return Promise.reject(new Error('track already added'));
      var transceiver;
      var localSdpObj; // Add the track id to the Set.

      this._trackIds.add(track.id);

      return Promise.resolve().then(function () {
        // Let's check if there is any inactive transceiver for same kind and
        // reuse it if so.
        transceiver = _this3._pc.getTransceivers().find(function (t) {
          return t.receiver.track.kind === track.kind && t.direction === 'inactive';
        });

        if (transceiver) {
          logger.debug('addProducer() | reusing an inactive transceiver');
          transceiver.direction = 'sendonly';
          return transceiver.sender.replaceTrack(track);
        } else {
          transceiver = _this3._pc.addTransceiver(track, {
            direction: 'sendonly'
          });
        }
      }).then(function () {
        var _transceiver = transceiver,
            sender = _transceiver.sender;
        var encodings = [];

        if (producer.simulcast) {
          logger.debug('addProducer() | enabling simulcast');

          if (producer.simulcast.high) {
            encodings.push({
              rid: "high".concat(_this3._nextRid),
              active: true,
              priority: 'low',
              maxBitrate: producer.simulcast.high
            });
          }

          if (producer.simulcast.medium) {
            encodings.push({
              rid: "medium".concat(_this3._nextRid),
              active: true,
              priority: 'medium',
              maxBitrate: producer.simulcast.medium
            });
          }

          if (producer.simulcast.low) {
            encodings.push({
              rid: "low".concat(_this3._nextRid),
              active: true,
              priority: 'high',
              maxBitrate: producer.simulcast.low
            });
          } // Update RID counter for future ones.


          _this3._nextRid++;
        }

        var parameters = sender.getParameters();
        return sender.setParameters(_objectSpread({}, parameters, {
          encodings: encodings
        }));
      }).then(function () {
        return _this3._pc.createOffer();
      }).then(function (offer) {
        logger.debug('addProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this3._pc.setLocalDescription(offer);
      }).then(function () {
        if (!_this3._transportReady) return _this3._setupTransport();
      }).then(function () {
        localSdpObj = _sdpTransform.default.parse(_this3._pc.localDescription.sdp);

        var remoteSdp = _this3._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('addProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this3._pc.setRemoteDescription(answer);
      }).then(function () {
        var rtpParameters = utils.clone(_this3._rtpParametersByKind[producer.kind]);
        sdpUnifiedPlanUtils.fillRtpParametersForTrack(rtpParameters, localSdpObj, track, {
          mid: transceiver.mid
        });
        return rtpParameters;
      }).catch(function (error) {
        // Panic here. Try to undo things.
        try {
          transceiver.direction = 'inactive';
        } catch (error2) {}

        _this3._trackIds.delete(track.id);

        throw error;
      });
    }
  }, {
    key: "removeProducer",
    value: function removeProducer(producer) {
      var _this4 = this;

      var track = producer.track;
      if (!this._trackIds.has(track.id)) return Promise.reject(new Error('track not found'));
      logger.debug('removeProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      return Promise.resolve().then(function () {
        // Get the associated RTCRtpSender.
        var rtpSender = _this4._pc.getSenders().find(function (s) {
          return s.track === track;
        });

        if (!rtpSender) throw new Error('local track not found');

        _this4._pc.removeTrack(rtpSender); // Remove the track id from the Set.


        _this4._trackIds.delete(track.id);

        return _this4._pc.createOffer();
      }).then(function (offer) {
        logger.debug('removeProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this4._pc.setLocalDescription(offer);
      }).then(function () {
        var localSdpObj = _sdpTransform.default.parse(_this4._pc.localDescription.sdp);

        var remoteSdp = _this4._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('removeProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this4._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "replaceProducerTrack",
    value: function replaceProducerTrack(producer, track) {
      var _this5 = this;

      logger.debug('replaceProducerTrack() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      var oldTrack = producer.track;
      return Promise.resolve().then(function () {
        // Get the associated RTCRtpSender.
        var rtpSender = _this5._pc.getSenders().find(function (s) {
          return s.track === oldTrack;
        });

        if (!rtpSender) throw new Error('local track not found');
        return rtpSender.replaceTrack(track);
      }).then(function () {
        // Remove the old track id from the Set.
        _this5._trackIds.delete(oldTrack.id); // Add the new track id to the Set.


        _this5._trackIds.add(track.id);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this6 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        return _this6._pc.createOffer({
          iceRestart: true
        });
      }).then(function (offer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this6._pc.setLocalDescription(offer);
      }).then(function () {
        var localSdpObj = _sdpTransform.default.parse(_this6._pc.localDescription.sdp);

        var remoteSdp = _this6._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this6._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this7 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // Get our local DTLS parameters.
        var transportLocalParameters = {};
        var sdp = _this7._pc.localDescription.sdp;

        var sdpObj = _sdpTransform.default.parse(sdp);

        var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj); // Let's decide that we'll be DTLS server (because we can).

        dtlsParameters.role = 'server';
        transportLocalParameters.dtlsParameters = dtlsParameters; // Provide the remote SDP handler with transport local parameters.

        _this7._remoteSdp.setTransportLocalParameters(transportLocalParameters); // We need transport remote parameters.


        return _this7.safeEmitAsPromise('@needcreatetransport', transportLocalParameters);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this7._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this7._transportReady = true;
      });
    }
  }]);

  return SendHandler;
}(Handler);

var RecvHandler =
/*#__PURE__*/
function (_Handler2) {
  _inherits(RecvHandler, _Handler2);

  function RecvHandler(rtpParametersByKind, settings) {
    var _this8;

    _classCallCheck(this, RecvHandler);

    _this8 = _possibleConstructorReturn(this, _getPrototypeOf(RecvHandler).call(this, 'recv', rtpParametersByKind, settings)); // Got transport remote parameters.
    // @type {Boolean}

    _this8._transportCreated = false; // Got transport local parameters.
    // @type {Boolean}

    _this8._transportUpdated = false; // Map of Consumers information indexed by consumer.id.
    // - mid {String}
    // - kind {String}
    // - closed {Boolean}
    // - trackId {String}
    // - ssrc {Number}
    // - rtxSsrc {Number}
    // - cname {String}
    // @type {Map<Number, Object>}

    _this8._consumerInfos = new Map();
    return _this8;
  }

  _createClass(RecvHandler, [{
    key: "addConsumer",
    value: function addConsumer(consumer) {
      var _this9 = this;

      logger.debug('addConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      if (this._consumerInfos.has(consumer.id)) return Promise.reject(new Error('Consumer already added'));
      var encoding = consumer.rtpParameters.encodings[0];
      var cname = consumer.rtpParameters.rtcp.cname;
      var consumerInfo = {
        mid: "".concat(consumer.kind[0]).concat(consumer.id),
        kind: consumer.kind,
        closed: consumer.closed,
        streamId: "recv-stream-".concat(consumer.id),
        trackId: "consumer-".concat(consumer.kind, "-").concat(consumer.id),
        ssrc: encoding.ssrc,
        cname: cname
      };
      if (encoding.rtx && encoding.rtx.ssrc) consumerInfo.rtxSsrc = encoding.rtx.ssrc;

      this._consumerInfos.set(consumer.id, consumerInfo);

      return Promise.resolve().then(function () {
        if (!_this9._transportCreated) return _this9._setupTransport();
      }).then(function () {
        var remoteSdp = _this9._remoteSdp.createOfferSdp(Array.from(_this9._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('addConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this9._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this9._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('addConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this9._pc.setLocalDescription(answer);
      }).then(function () {
        if (!_this9._transportUpdated) return _this9._updateTransport();
      }).then(function () {
        var transceiver = _this9._pc.getTransceivers().find(function (t) {
          return t.mid === consumerInfo.mid;
        });

        if (!transceiver) throw new Error('remote track not found');
        return transceiver.receiver.track;
      });
    }
  }, {
    key: "removeConsumer",
    value: function removeConsumer(consumer) {
      var _this10 = this;

      logger.debug('removeConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);

      var consumerInfo = this._consumerInfos.get(consumer.id);

      if (!consumerInfo) return Promise.reject(new Error('Consumer not found'));
      consumerInfo.closed = true;
      return Promise.resolve().then(function () {
        var remoteSdp = _this10._remoteSdp.createOfferSdp(Array.from(_this10._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('removeConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this10._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this10._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('removeConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this10._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this11 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        var remoteSdp = _this11._remoteSdp.createOfferSdp(Array.from(_this11._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this11._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this11._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this11._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this12 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // We need transport remote parameters.
        return _this12.safeEmitAsPromise('@needcreatetransport', null);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this12._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this12._transportCreated = true;
      });
    }
  }, {
    key: "_updateTransport",
    value: function _updateTransport() {
      logger.debug('_updateTransport()'); // Get our local DTLS parameters.

      var sdp = this._pc.localDescription.sdp;

      var sdpObj = _sdpTransform.default.parse(sdp);

      var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj);
      var transportLocalParameters = {
        dtlsParameters: dtlsParameters
      }; // We need to provide transport local parameters.

      this.safeEmit('@needupdatetransport', transportLocalParameters);
      this._transportUpdated = true;
    }
  }]);

  return RecvHandler;
}(Handler);

var Firefox65 =
/*#__PURE__*/
function () {
  _createClass(Firefox65, null, [{
    key: "getNativeRtpCapabilities",
    value: function getNativeRtpCapabilities() {
      logger.debug('getNativeRtpCapabilities()');
      var pc = new RTCPeerConnection({
        iceServers: [],
        iceTransportPolicy: 'all',
        bundlePolicy: 'max-bundle',
        rtcpMuxPolicy: 'require'
      }); // NOTE: We need to add a real video track to get the RID extension mapping.

      var canvas = document.createElement('canvas'); // NOTE: Otherwise Firefox fails in next line.

      canvas.getContext('2d');
      var fakeStream = canvas.captureStream();
      var fakeVideoTrack = fakeStream.getVideoTracks()[0];
      var rtpSender = pc.addTrack(fakeVideoTrack, fakeStream);
      rtpSender.setParameters({
        encodings: [{
          rid: 'RID1',
          maxBitrate: 40000
        }, {
          rid: 'RID2',
          maxBitrate: 10000
        }]
      });
      return pc.createOffer({
        offerToReceiveAudio: true,
        offerToReceiveVideo: true
      }).then(function (offer) {
        try {
          canvas.remove();
        } catch (error) {}

        try {
          fakeVideoTrack.stop();
        } catch (error) {}

        try {
          pc.close();
        } catch (error) {}

        var sdpObj = _sdpTransform.default.parse(offer.sdp);

        var nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities(sdpObj);
        return nativeRtpCapabilities;
      }).catch(function (error) {
        try {
          canvas.remove();
        } catch (error2) {}

        try {
          fakeVideoTrack.stop();
        } catch (error2) {}

        try {
          pc.close();
        } catch (error2) {}

        throw error;
      });
    }
  }, {
    key: "tag",
    get: function get() {
      return 'Firefox65';
    }
  }]);

  function Firefox65(direction, extendedRtpCapabilities, settings) {
    _classCallCheck(this, Firefox65);

    logger.debug('constructor() [direction:%s, extendedRtpCapabilities:%o]', direction, extendedRtpCapabilities);
    var rtpParametersByKind;

    switch (direction) {
      case 'send':
        {
          rtpParametersByKind = {
            audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
          };
          return new SendHandler(rtpParametersByKind, settings);
        }

      case 'recv':
        {
          rtpParametersByKind = {
            audio: ortc.getReceivingFullRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getReceivingFullRtpParameters('video', extendedRtpCapabilities)
          };
          return new RecvHandler(rtpParametersByKind, settings);
        }
    }
  }

  return Firefox65;
}();

exports.default = Firefox65;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/ReactNative.js":
/*!***********************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/ReactNative.js ***!
  \***********************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _sdpTransform = _interopRequireDefault(__webpack_require__(/*! sdp-transform */ "./node_modules/sdp-transform/lib/index.js"));

var _Logger = _interopRequireDefault(__webpack_require__(/*! ../Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter2 = _interopRequireDefault(__webpack_require__(/*! ../EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

var utils = _interopRequireWildcard(__webpack_require__(/*! ../utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

var ortc = _interopRequireWildcard(__webpack_require__(/*! ../ortc */ "./node_modules/mediasoup-client/lib-es5/ortc.js"));

var sdpCommonUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/commonUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/commonUtils.js"));

var sdpPlanBUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/planBUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/planBUtils.js"));

var _RemotePlanBSdp = _interopRequireDefault(__webpack_require__(/*! ./sdp/RemotePlanBSdp */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemotePlanBSdp.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var logger = new _Logger.default('ReactNative');

var Handler =
/*#__PURE__*/
function (_EnhancedEventEmitter) {
  _inherits(Handler, _EnhancedEventEmitter);

  function Handler(direction, rtpParametersByKind, settings) {
    var _this;

    _classCallCheck(this, Handler);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(Handler).call(this, logger)); // RTCPeerConnection instance.
    // @type {RTCPeerConnection}

    _this._pc = new RTCPeerConnection({
      iceServers: settings.turnServers || [],
      iceTransportPolicy: settings.iceTransportPolicy,
      bundlePolicy: 'max-bundle',
      rtcpMuxPolicy: 'require'
    }); // Generic sending RTP parameters for audio and video.
    // @type {Object}

    _this._rtpParametersByKind = rtpParametersByKind; // Remote SDP handler.
    // @type {RemotePlanBSdp}

    _this._remoteSdp = new _RemotePlanBSdp.default(direction, rtpParametersByKind); // Handle RTCPeerConnection connection status.

    _this._pc.addEventListener('iceconnectionstatechange', function () {
      switch (_this._pc.iceConnectionState) {
        case 'checking':
          _this.emit('@connectionstatechange', 'connecting');

          break;

        case 'connected':
        case 'completed':
          _this.emit('@connectionstatechange', 'connected');

          break;

        case 'failed':
          _this.emit('@connectionstatechange', 'failed');

          break;

        case 'disconnected':
          _this.emit('@connectionstatechange', 'disconnected');

          break;

        case 'closed':
          _this.emit('@connectionstatechange', 'closed');

          break;
      }
    });

    return _this;
  }

  _createClass(Handler, [{
    key: "close",
    value: function close() {
      logger.debug('close()'); // Close RTCPeerConnection.

      try {
        this._pc.close();
      } catch (error) {}
    }
  }, {
    key: "remoteClosed",
    value: function remoteClosed() {
      logger.debug('remoteClosed()');
      this._transportReady = false;
      if (this._transportUpdated) this._transportUpdated = false;
    }
  }]);

  return Handler;
}(_EnhancedEventEmitter2.default);

var SendHandler =
/*#__PURE__*/
function (_Handler) {
  _inherits(SendHandler, _Handler);

  function SendHandler(rtpParametersByKind, settings) {
    var _this2;

    _classCallCheck(this, SendHandler);

    _this2 = _possibleConstructorReturn(this, _getPrototypeOf(SendHandler).call(this, 'send', rtpParametersByKind, settings)); // Got transport local and remote parameters.
    // @type {Boolean}

    _this2._transportReady = false; // Handled tracks.
    // @type {Set<MediaStreamTrack>}

    _this2._tracks = new Set();
    return _this2;
  }

  _createClass(SendHandler, [{
    key: "addProducer",
    value: function addProducer(producer) {
      var _this3 = this;

      var track = producer.track;
      logger.debug('addProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      if (this._tracks.has(track)) return Promise.reject(new Error('track already added'));
      if (!track.streamReactTag) return Promise.reject(new Error('no track.streamReactTag property'));
      var stream;
      var localSdpObj;
      return Promise.resolve().then(function () {
        // Add the track to the Set.
        _this3._tracks.add(track); // Hack: Create a new stream with track.streamReactTag as id.


        stream = new MediaStream(track.streamReactTag); // Add the track to the stream.

        stream.addTrack(track); // Add the stream to the PeerConnection.

        _this3._pc.addStream(stream);

        return _this3._pc.createOffer();
      }).then(function (offer) {
        // If simulcast is set, mangle the offer.
        if (producer.simulcast) {
          logger.debug('addProducer() | enabling simulcast');

          var sdpObject = _sdpTransform.default.parse(offer.sdp);

          sdpPlanBUtils.addSimulcastForTrack(sdpObject, track);

          var offerSdp = _sdpTransform.default.write(sdpObject);

          offer = {
            type: 'offer',
            sdp: offerSdp
          };
        }

        logger.debug('addProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        var offerDesc = new RTCSessionDescription(offer);
        return _this3._pc.setLocalDescription(offerDesc);
      }).then(function () {
        if (!_this3._transportReady) return _this3._setupTransport();
      }).then(function () {
        localSdpObj = _sdpTransform.default.parse(_this3._pc.localDescription.sdp);

        var remoteSdp = _this3._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('addProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        var answerDesc = new RTCSessionDescription(answer);
        return _this3._pc.setRemoteDescription(answerDesc);
      }).then(function () {
        var rtpParameters = utils.clone(_this3._rtpParametersByKind[producer.kind]); // Fill the RTP parameters for this track.

        sdpPlanBUtils.fillRtpParametersForTrack(rtpParameters, localSdpObj, track);
        return rtpParameters;
      }).catch(function (error) {
        // Panic here. Try to undo things.
        _this3._tracks.delete(track);

        stream.removeTrack(track);

        _this3._pc.removeStream(stream);

        throw error;
      });
    }
  }, {
    key: "removeProducer",
    value: function removeProducer(producer) {
      var _this4 = this;

      var track = producer.track;
      logger.debug('removeProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      if (!track.streamReactTag) return Promise.reject(new Error('no track.streamReactTag property'));
      return Promise.resolve().then(function () {
        // Remove the track from the Set.
        _this4._tracks.delete(track); // Hack: Create a new stream with track.streamReactTag as id.


        var stream = new MediaStream(track.streamReactTag); // Add the track to the stream.

        stream.addTrack(track); // Remove the stream from the PeerConnection.

        _this4._pc.removeStream(stream);

        return _this4._pc.createOffer();
      }).then(function (offer) {
        logger.debug('removeProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this4._pc.setLocalDescription(offer);
      }).catch(function (error) {
        // NOTE: If there are no sending tracks, setLocalDescription() will fail with
        // "Failed to create channels". If so, ignore it.
        if (_this4._tracks.size === 0) {
          logger.warn('removeProducer() | ignoring expected error due no sending tracks: %s', error.toString());
          return;
        }

        throw error;
      }).then(function () {
        if (_this4._pc.signalingState === 'stable') return;

        var localSdpObj = _sdpTransform.default.parse(_this4._pc.localDescription.sdp);

        var remoteSdp = _this4._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('removeProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        var answerDesc = new RTCSessionDescription(answer);
        return _this4._pc.setRemoteDescription(answerDesc);
      });
    }
  }, {
    key: "replaceProducerTrack",
    value: function replaceProducerTrack(producer, track) {
      var _this5 = this;

      logger.debug('replaceProducerTrack() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      if (!track.streamReactTag) return Promise.reject(new Error('no track.streamReactTag property'));
      var oldTrack = producer.track;
      var stream;
      var localSdpObj;
      return Promise.resolve().then(function () {
        // Add the new Track to the Set and remove the old one.
        _this5._tracks.add(track);

        _this5._tracks.delete(oldTrack); // Hack: Create a new stream with track.streamReactTag as id.


        stream = new MediaStream(track.streamReactTag); // Add the track to the stream and remove the old one.

        stream.addTrack(track);
        stream.removeTrack(oldTrack); // Add the stream to the PeerConnection.

        _this5._pc.addStream(stream);

        return _this5._pc.createOffer();
      }).then(function (offer) {
        // If simulcast is set, mangle the offer.
        if (producer.simulcast) {
          logger.debug('addProducer() | enabling simulcast');

          var sdpObject = _sdpTransform.default.parse(offer.sdp);

          sdpPlanBUtils.addSimulcastForTrack(sdpObject, track);

          var offerSdp = _sdpTransform.default.write(sdpObject);

          offer = {
            type: 'offer',
            sdp: offerSdp
          };
        }

        logger.debug('replaceProducerTrack() | calling pc.setLocalDescription() [offer:%o]', offer);
        var offerDesc = new RTCSessionDescription(offer);
        return _this5._pc.setLocalDescription(offerDesc);
      }).then(function () {
        localSdpObj = _sdpTransform.default.parse(_this5._pc.localDescription.sdp);

        var remoteSdp = _this5._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('replaceProducerTrack() | calling pc.setRemoteDescription() [answer:%o]', answer);
        var answerDesc = new RTCSessionDescription(answer);
        return _this5._pc.setRemoteDescription(answerDesc);
      }).then(function () {
        var rtpParameters = utils.clone(_this5._rtpParametersByKind[producer.kind]); // Fill the RTP parameters for the new track.

        sdpPlanBUtils.fillRtpParametersForTrack(rtpParameters, localSdpObj, track); // We need to provide new RTP parameters.

        _this5.safeEmit('@needupdateproducer', producer, rtpParameters);
      }).catch(function (error) {
        // Panic here. Try to undo things.
        _this5._tracks.delete(track);

        stream.removeTrack(track);

        _this5._pc.addStream(stream);

        throw error;
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this6 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        return _this6._pc.createOffer({
          iceRestart: true
        });
      }).then(function (offer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this6._pc.setLocalDescription(offer);
      }).then(function () {
        var localSdpObj = _sdpTransform.default.parse(_this6._pc.localDescription.sdp);

        var remoteSdp = _this6._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
        var answerDesc = new RTCSessionDescription(answer);
        return _this6._pc.setRemoteDescription(answerDesc);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this7 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // Get our local DTLS parameters.
        var transportLocalParameters = {};
        var sdp = _this7._pc.localDescription.sdp;

        var sdpObj = _sdpTransform.default.parse(sdp);

        var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj); // Let's decide that we'll be DTLS server (because we can).

        dtlsParameters.role = 'server';
        transportLocalParameters.dtlsParameters = dtlsParameters; // Provide the remote SDP handler with transport local parameters.

        _this7._remoteSdp.setTransportLocalParameters(transportLocalParameters); // We need transport remote parameters.


        return _this7.safeEmitAsPromise('@needcreatetransport', transportLocalParameters);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this7._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this7._transportReady = true;
      });
    }
  }]);

  return SendHandler;
}(Handler);

var RecvHandler =
/*#__PURE__*/
function (_Handler2) {
  _inherits(RecvHandler, _Handler2);

  function RecvHandler(rtpParametersByKind, settings) {
    var _this8;

    _classCallCheck(this, RecvHandler);

    _this8 = _possibleConstructorReturn(this, _getPrototypeOf(RecvHandler).call(this, 'recv', rtpParametersByKind, settings)); // Got transport remote parameters.
    // @type {Boolean}

    _this8._transportCreated = false; // Got transport local parameters.
    // @type {Boolean}

    _this8._transportUpdated = false; // Seen media kinds.
    // @type {Set<String>}

    _this8._kinds = new Set(); // Map of Consumers information indexed by consumer.id.
    // - kind {String}
    // - trackId {String}
    // - ssrc {Number}
    // - rtxSsrc {Number}
    // - cname {String}
    // @type {Map<Number, Object>}

    _this8._consumerInfos = new Map();
    return _this8;
  }

  _createClass(RecvHandler, [{
    key: "addConsumer",
    value: function addConsumer(consumer) {
      var _this9 = this;

      logger.debug('addConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      if (this._consumerInfos.has(consumer.id)) return Promise.reject(new Error('Consumer already added'));
      var encoding = consumer.rtpParameters.encodings[0];
      var cname = consumer.rtpParameters.rtcp.cname;
      var consumerInfo = {
        kind: consumer.kind,
        streamId: "recv-stream-".concat(consumer.id),
        trackId: "consumer-".concat(consumer.kind, "-").concat(consumer.id),
        ssrc: encoding.ssrc,
        cname: cname
      };
      if (encoding.rtx && encoding.rtx.ssrc) consumerInfo.rtxSsrc = encoding.rtx.ssrc;

      this._consumerInfos.set(consumer.id, consumerInfo);

      this._kinds.add(consumer.kind);

      return Promise.resolve().then(function () {
        if (!_this9._transportCreated) return _this9._setupTransport();
      }).then(function () {
        var remoteSdp = _this9._remoteSdp.createOfferSdp(Array.from(_this9._kinds), Array.from(_this9._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('addConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        var offerDesc = new RTCSessionDescription(offer);
        return _this9._pc.setRemoteDescription(offerDesc);
      }).then(function () {
        return _this9._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('addConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this9._pc.setLocalDescription(answer);
      }).then(function () {
        if (!_this9._transportUpdated) return _this9._updateTransport();
      }).then(function () {
        var stream = _this9._pc.getRemoteStreams().find(function (s) {
          return s.id === consumerInfo.streamId;
        });

        var track = stream.getTrackById(consumerInfo.trackId); // Hack: Add a streamReactTag property with the reactTag of the MediaStream
        // generated by react-native-webrtc (this is needed because react-native-webrtc
        // assumes that we're gonna use the streams generated by it).

        track.streamReactTag = stream.reactTag;
        if (!track) throw new Error('remote track not found');
        return track;
      });
    }
  }, {
    key: "removeConsumer",
    value: function removeConsumer(consumer) {
      var _this10 = this;

      logger.debug('removeConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      if (!this._consumerInfos.has(consumer.id)) return Promise.reject(new Error('Consumer not found'));

      this._consumerInfos.delete(consumer.id);

      return Promise.resolve().then(function () {
        var remoteSdp = _this10._remoteSdp.createOfferSdp(Array.from(_this10._kinds), Array.from(_this10._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('removeConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        var offerDesc = new RTCSessionDescription(offer);
        return _this10._pc.setRemoteDescription(offerDesc);
      }).then(function () {
        return _this10._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('removeConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this10._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this11 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        var remoteSdp = _this11._remoteSdp.createOfferSdp(Array.from(_this11._kinds), Array.from(_this11._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
        var offerDesc = new RTCSessionDescription(offer);
        return _this11._pc.setRemoteDescription(offerDesc);
      }).then(function () {
        return _this11._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this11._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this12 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // We need transport remote parameters.
        return _this12.safeEmitAsPromise('@needcreatetransport', null);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this12._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this12._transportCreated = true;
      });
    }
  }, {
    key: "_updateTransport",
    value: function _updateTransport() {
      logger.debug('_updateTransport()'); // Get our local DTLS parameters.

      var sdp = this._pc.localDescription.sdp;

      var sdpObj = _sdpTransform.default.parse(sdp);

      var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj);
      var transportLocalParameters = {
        dtlsParameters: dtlsParameters
      }; // We need to provide transport local parameters.

      this.safeEmit('@needupdatetransport', transportLocalParameters);
      this._transportUpdated = true;
    }
  }]);

  return RecvHandler;
}(Handler);

var ReactNative =
/*#__PURE__*/
function () {
  _createClass(ReactNative, null, [{
    key: "getNativeRtpCapabilities",
    value: function getNativeRtpCapabilities() {
      logger.debug('getNativeRtpCapabilities()');
      var pc = new RTCPeerConnection({
        iceServers: [],
        iceTransportPolicy: 'all',
        bundlePolicy: 'max-bundle',
        rtcpMuxPolicy: 'require'
      });
      return pc.createOffer({
        offerToReceiveAudio: true,
        offerToReceiveVideo: true
      }).then(function (offer) {
        try {
          pc.close();
        } catch (error) {}

        var sdpObj = _sdpTransform.default.parse(offer.sdp);

        var nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities(sdpObj);
        return nativeRtpCapabilities;
      }).catch(function (error) {
        try {
          pc.close();
        } catch (error2) {}

        throw error;
      });
    }
  }, {
    key: "tag",
    get: function get() {
      return 'ReactNative';
    }
  }]);

  function ReactNative(direction, extendedRtpCapabilities, settings) {
    _classCallCheck(this, ReactNative);

    logger.debug('constructor() [direction:%s, extendedRtpCapabilities:%o]', direction, extendedRtpCapabilities);
    var rtpParametersByKind;

    switch (direction) {
      case 'send':
        {
          rtpParametersByKind = {
            audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
          };
          return new SendHandler(rtpParametersByKind, settings);
        }

      case 'recv':
        {
          rtpParametersByKind = {
            audio: ortc.getReceivingFullRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getReceivingFullRtpParameters('video', extendedRtpCapabilities)
          };
          return new RecvHandler(rtpParametersByKind, settings);
        }
    }
  }

  return ReactNative;
}();

exports.default = ReactNative;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/Safari11.js":
/*!********************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/Safari11.js ***!
  \********************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _sdpTransform = _interopRequireDefault(__webpack_require__(/*! sdp-transform */ "./node_modules/sdp-transform/lib/index.js"));

var _Logger = _interopRequireDefault(__webpack_require__(/*! ../Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter2 = _interopRequireDefault(__webpack_require__(/*! ../EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

var utils = _interopRequireWildcard(__webpack_require__(/*! ../utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

var ortc = _interopRequireWildcard(__webpack_require__(/*! ../ortc */ "./node_modules/mediasoup-client/lib-es5/ortc.js"));

var sdpCommonUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/commonUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/commonUtils.js"));

var sdpPlanBUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/planBUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/planBUtils.js"));

var _RemotePlanBSdp = _interopRequireDefault(__webpack_require__(/*! ./sdp/RemotePlanBSdp */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemotePlanBSdp.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var logger = new _Logger.default('Safari11');

var Handler =
/*#__PURE__*/
function (_EnhancedEventEmitter) {
  _inherits(Handler, _EnhancedEventEmitter);

  function Handler(direction, rtpParametersByKind, settings) {
    var _this;

    _classCallCheck(this, Handler);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(Handler).call(this, logger)); // RTCPeerConnection instance.
    // @type {RTCPeerConnection}

    _this._pc = new RTCPeerConnection({
      iceServers: settings.turnServers || [],
      iceTransportPolicy: settings.iceTransportPolicy,
      bundlePolicy: 'max-bundle',
      rtcpMuxPolicy: 'require'
    }); // Generic sending RTP parameters for audio and video.
    // @type {Object}

    _this._rtpParametersByKind = rtpParametersByKind; // Remote SDP handler.
    // @type {RemotePlanBSdp}

    _this._remoteSdp = new _RemotePlanBSdp.default(direction, rtpParametersByKind); // Handle RTCPeerConnection connection status.

    _this._pc.addEventListener('iceconnectionstatechange', function () {
      switch (_this._pc.iceConnectionState) {
        case 'checking':
          _this.emit('@connectionstatechange', 'connecting');

          break;

        case 'connected':
        case 'completed':
          _this.emit('@connectionstatechange', 'connected');

          break;

        case 'failed':
          _this.emit('@connectionstatechange', 'failed');

          break;

        case 'disconnected':
          _this.emit('@connectionstatechange', 'disconnected');

          break;

        case 'closed':
          _this.emit('@connectionstatechange', 'closed');

          break;
      }
    });

    return _this;
  }

  _createClass(Handler, [{
    key: "close",
    value: function close() {
      logger.debug('close()'); // Close RTCPeerConnection.

      try {
        this._pc.close();
      } catch (error) {}
    }
  }, {
    key: "remoteClosed",
    value: function remoteClosed() {
      logger.debug('remoteClosed()');
      this._transportReady = false;
      if (this._transportUpdated) this._transportUpdated = false;
    }
  }]);

  return Handler;
}(_EnhancedEventEmitter2.default);

var SendHandler =
/*#__PURE__*/
function (_Handler) {
  _inherits(SendHandler, _Handler);

  function SendHandler(rtpParametersByKind, settings) {
    var _this2;

    _classCallCheck(this, SendHandler);

    _this2 = _possibleConstructorReturn(this, _getPrototypeOf(SendHandler).call(this, 'send', rtpParametersByKind, settings)); // Got transport local and remote parameters.
    // @type {Boolean}

    _this2._transportReady = false; // Local stream.
    // @type {MediaStream}

    _this2._stream = new MediaStream();
    return _this2;
  }

  _createClass(SendHandler, [{
    key: "addProducer",
    value: function addProducer(producer) {
      var _this3 = this;

      var track = producer.track;
      logger.debug('addProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      if (this._stream.getTrackById(track.id)) return Promise.reject(new Error('track already added'));
      var rtpSender;
      var localSdpObj;
      return Promise.resolve().then(function () {
        // Add the track to the local stream.
        _this3._stream.addTrack(track); // Add the stream to the PeerConnection.


        rtpSender = _this3._pc.addTrack(track, _this3._stream);
        return _this3._pc.createOffer();
      }).then(function (offer) {
        logger.debug('addProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this3._pc.setLocalDescription(offer);
      }).then(function () {
        if (!_this3._transportReady) return _this3._setupTransport();
      }).then(function () {
        localSdpObj = _sdpTransform.default.parse(_this3._pc.localDescription.sdp);

        var remoteSdp = _this3._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('addProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this3._pc.setRemoteDescription(answer);
      }).then(function () {
        var rtpParameters = utils.clone(_this3._rtpParametersByKind[producer.kind]); // Fill the RTP parameters for this track.

        sdpPlanBUtils.fillRtpParametersForTrack(rtpParameters, localSdpObj, track);
        return rtpParameters;
      }).catch(function (error) {
        // Panic here. Try to undo things.
        try {
          _this3._pc.removeTrack(rtpSender);
        } catch (error2) {}

        _this3._stream.removeTrack(track);

        throw error;
      });
    }
  }, {
    key: "removeProducer",
    value: function removeProducer(producer) {
      var _this4 = this;

      var track = producer.track;
      logger.debug('removeProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      return Promise.resolve().then(function () {
        // Get the associated RTCRtpSender.
        var rtpSender = _this4._pc.getSenders().find(function (s) {
          return s.track === track;
        });

        if (!rtpSender) throw new Error('RTCRtpSender not found'); // Remove the associated RtpSender.

        _this4._pc.removeTrack(rtpSender); // Remove the track from the local stream.


        _this4._stream.removeTrack(track);

        return _this4._pc.createOffer();
      }).then(function (offer) {
        logger.debug('removeProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this4._pc.setLocalDescription(offer);
      }).catch(function (error) {
        // NOTE: If there are no sending tracks, setLocalDescription() will fail with
        // "Failed to create channels". If so, ignore it.
        if (_this4._stream.getTracks().length === 0) {
          logger.warn('removeProducer() | ignoring expected error due no sending tracks: %s', error.toString());
          return;
        }

        throw error;
      }).then(function () {
        if (_this4._pc.signalingState === 'stable') return;

        var localSdpObj = _sdpTransform.default.parse(_this4._pc.localDescription.sdp);

        var remoteSdp = _this4._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('removeProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this4._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "replaceProducerTrack",
    value: function replaceProducerTrack(producer, track) {
      var _this5 = this;

      logger.debug('replaceProducerTrack() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      var oldTrack = producer.track;
      return Promise.resolve().then(function () {
        // Get the associated RTCRtpSender.
        var rtpSender = _this5._pc.getSenders().find(function (s) {
          return s.track === oldTrack;
        });

        if (!rtpSender) throw new Error('local track not found');
        return rtpSender.replaceTrack(track);
      }).then(function () {
        // Remove the old track from the local stream.
        _this5._stream.removeTrack(oldTrack); // Add the new track to the local stream.


        _this5._stream.addTrack(track);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this6 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        return _this6._pc.createOffer({
          iceRestart: true
        });
      }).then(function (offer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this6._pc.setLocalDescription(offer);
      }).then(function () {
        var localSdpObj = _sdpTransform.default.parse(_this6._pc.localDescription.sdp);

        var remoteSdp = _this6._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this6._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this7 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // Get our local DTLS parameters.
        var transportLocalParameters = {};
        var sdp = _this7._pc.localDescription.sdp;

        var sdpObj = _sdpTransform.default.parse(sdp);

        var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj); // Let's decide that we'll be DTLS server (because we can).

        dtlsParameters.role = 'server';
        transportLocalParameters.dtlsParameters = dtlsParameters; // Provide the remote SDP handler with transport local parameters.

        _this7._remoteSdp.setTransportLocalParameters(transportLocalParameters); // We need transport remote parameters.


        return _this7.safeEmitAsPromise('@needcreatetransport', transportLocalParameters);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this7._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this7._transportReady = true;
      });
    }
  }]);

  return SendHandler;
}(Handler);

var RecvHandler =
/*#__PURE__*/
function (_Handler2) {
  _inherits(RecvHandler, _Handler2);

  function RecvHandler(rtpParametersByKind, settings) {
    var _this8;

    _classCallCheck(this, RecvHandler);

    _this8 = _possibleConstructorReturn(this, _getPrototypeOf(RecvHandler).call(this, 'recv', rtpParametersByKind, settings)); // Got transport remote parameters.
    // @type {Boolean}

    _this8._transportCreated = false; // Got transport local parameters.
    // @type {Boolean}

    _this8._transportUpdated = false; // Seen media kinds.
    // @type {Set<String>}

    _this8._kinds = new Set(); // Map of Consumers information indexed by consumer.id.
    // - kind {String}
    // - trackId {String}
    // - ssrc {Number}
    // - rtxSsrc {Number}
    // - cname {String}
    // @type {Map<Number, Object>}

    _this8._consumerInfos = new Map();
    return _this8;
  }

  _createClass(RecvHandler, [{
    key: "addConsumer",
    value: function addConsumer(consumer) {
      var _this9 = this;

      logger.debug('addConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      if (this._consumerInfos.has(consumer.id)) return Promise.reject(new Error('Consumer already added'));
      var encoding = consumer.rtpParameters.encodings[0];
      var cname = consumer.rtpParameters.rtcp.cname;
      var consumerInfo = {
        kind: consumer.kind,
        streamId: "recv-stream-".concat(consumer.id),
        trackId: "consumer-".concat(consumer.kind, "-").concat(consumer.id),
        ssrc: encoding.ssrc,
        cname: cname
      };
      if (encoding.rtx && encoding.rtx.ssrc) consumerInfo.rtxSsrc = encoding.rtx.ssrc;

      this._consumerInfos.set(consumer.id, consumerInfo);

      this._kinds.add(consumer.kind);

      return Promise.resolve().then(function () {
        if (!_this9._transportCreated) return _this9._setupTransport();
      }).then(function () {
        var remoteSdp = _this9._remoteSdp.createOfferSdp(Array.from(_this9._kinds), Array.from(_this9._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('addConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this9._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this9._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('addConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this9._pc.setLocalDescription(answer);
      }).then(function () {
        if (!_this9._transportUpdated) return _this9._updateTransport();
      }).then(function () {
        var newRtpReceiver = _this9._pc.getReceivers().find(function (rtpReceiver) {
          var track = rtpReceiver.track;
          if (!track) return false;
          return track.id === consumerInfo.trackId;
        });

        if (!newRtpReceiver) throw new Error('remote track not found');
        return newRtpReceiver.track;
      });
    }
  }, {
    key: "removeConsumer",
    value: function removeConsumer(consumer) {
      var _this10 = this;

      logger.debug('removeConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      if (!this._consumerInfos.has(consumer.id)) return Promise.reject(new Error('Consumer not found'));

      this._consumerInfos.delete(consumer.id);

      return Promise.resolve().then(function () {
        var remoteSdp = _this10._remoteSdp.createOfferSdp(Array.from(_this10._kinds), Array.from(_this10._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('removeConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this10._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this10._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('removeConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this10._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this11 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        var remoteSdp = _this11._remoteSdp.createOfferSdp(Array.from(_this11._kinds), Array.from(_this11._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this11._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this11._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this11._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this12 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // We need transport remote parameters.
        return _this12.safeEmitAsPromise('@needcreatetransport', null);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this12._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this12._transportCreated = true;
      });
    }
  }, {
    key: "_updateTransport",
    value: function _updateTransport() {
      logger.debug('_updateTransport()'); // Get our local DTLS parameters.

      var sdp = this._pc.localDescription.sdp;

      var sdpObj = _sdpTransform.default.parse(sdp);

      var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj);
      var transportLocalParameters = {
        dtlsParameters: dtlsParameters
      }; // We need to provide transport local parameters.

      this.safeEmit('@needupdatetransport', transportLocalParameters);
      this._transportUpdated = true;
    }
  }]);

  return RecvHandler;
}(Handler);

var Safari11 =
/*#__PURE__*/
function () {
  _createClass(Safari11, null, [{
    key: "getNativeRtpCapabilities",
    value: function getNativeRtpCapabilities() {
      logger.debug('getNativeRtpCapabilities()');
      var pc = new RTCPeerConnection({
        iceServers: [],
        iceTransportPolicy: 'all',
        bundlePolicy: 'max-bundle',
        rtcpMuxPolicy: 'require'
      });
      pc.addTransceiver('audio');
      pc.addTransceiver('video');
      return pc.createOffer().then(function (offer) {
        try {
          pc.close();
        } catch (error) {}

        var sdpObj = _sdpTransform.default.parse(offer.sdp);

        var nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities(sdpObj);
        return nativeRtpCapabilities;
      }).catch(function (error) {
        try {
          pc.close();
        } catch (error2) {}

        throw error;
      });
    }
  }, {
    key: "tag",
    get: function get() {
      return 'Safari11';
    }
  }]);

  function Safari11(direction, extendedRtpCapabilities, settings) {
    _classCallCheck(this, Safari11);

    logger.debug('constructor() [direction:%s, extendedRtpCapabilities:%o]', direction, extendedRtpCapabilities);
    var rtpParametersByKind;

    switch (direction) {
      case 'send':
        {
          rtpParametersByKind = {
            audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
          };
          return new SendHandler(rtpParametersByKind, settings);
        }

      case 'recv':
        {
          rtpParametersByKind = {
            audio: ortc.getReceivingFullRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getReceivingFullRtpParameters('video', extendedRtpCapabilities)
          };
          return new RecvHandler(rtpParametersByKind, settings);
        }
    }
  }

  return Safari11;
}();

exports.default = Safari11;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/Safari12.js":
/*!********************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/Safari12.js ***!
  \********************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _sdpTransform = _interopRequireDefault(__webpack_require__(/*! sdp-transform */ "./node_modules/sdp-transform/lib/index.js"));

var _Logger = _interopRequireDefault(__webpack_require__(/*! ../Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter2 = _interopRequireDefault(__webpack_require__(/*! ../EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

var utils = _interopRequireWildcard(__webpack_require__(/*! ../utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

var ortc = _interopRequireWildcard(__webpack_require__(/*! ../ortc */ "./node_modules/mediasoup-client/lib-es5/ortc.js"));

var sdpCommonUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/commonUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/commonUtils.js"));

var sdpUnifiedPlanUtils = _interopRequireWildcard(__webpack_require__(/*! ./sdp/unifiedPlanUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/unifiedPlanUtils.js"));

var _RemoteUnifiedPlanSdp = _interopRequireDefault(__webpack_require__(/*! ./sdp/RemoteUnifiedPlanSdp */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemoteUnifiedPlanSdp.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var logger = new _Logger.default('Safari12');

var Handler =
/*#__PURE__*/
function (_EnhancedEventEmitter) {
  _inherits(Handler, _EnhancedEventEmitter);

  function Handler(direction, rtpParametersByKind, settings) {
    var _this;

    _classCallCheck(this, Handler);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(Handler).call(this, logger)); // RTCPeerConnection instance.
    // @type {RTCPeerConnection}

    _this._pc = new RTCPeerConnection({
      iceServers: settings.turnServers || [],
      iceTransportPolicy: settings.iceTransportPolicy,
      bundlePolicy: 'max-bundle',
      rtcpMuxPolicy: 'require'
    }); // Generic sending RTP parameters for audio and video.
    // @type {Object}

    _this._rtpParametersByKind = rtpParametersByKind; // Remote SDP handler.
    // @type {RemoteUnifiedPlanSdp}

    _this._remoteSdp = new _RemoteUnifiedPlanSdp.default(direction, rtpParametersByKind); // Handle RTCPeerConnection connection status.

    _this._pc.addEventListener('iceconnectionstatechange', function () {
      switch (_this._pc.iceConnectionState) {
        case 'checking':
          _this.emit('@connectionstatechange', 'connecting');

          break;

        case 'connected':
        case 'completed':
          _this.emit('@connectionstatechange', 'connected');

          break;

        case 'failed':
          _this.emit('@connectionstatechange', 'failed');

          break;

        case 'disconnected':
          _this.emit('@connectionstatechange', 'disconnected');

          break;

        case 'closed':
          _this.emit('@connectionstatechange', 'closed');

          break;
      }
    });

    return _this;
  }

  _createClass(Handler, [{
    key: "close",
    value: function close() {
      logger.debug('close()'); // Close RTCPeerConnection.

      try {
        this._pc.close();
      } catch (error) {}
    }
  }, {
    key: "remoteClosed",
    value: function remoteClosed() {
      logger.debug('remoteClosed()');
      this._transportReady = false;
      if (this._transportUpdated) this._transportUpdated = false;
    }
  }]);

  return Handler;
}(_EnhancedEventEmitter2.default);

var SendHandler =
/*#__PURE__*/
function (_Handler) {
  _inherits(SendHandler, _Handler);

  function SendHandler(rtpParametersByKind, settings) {
    var _this2;

    _classCallCheck(this, SendHandler);

    _this2 = _possibleConstructorReturn(this, _getPrototypeOf(SendHandler).call(this, 'send', rtpParametersByKind, settings)); // Got transport local and remote parameters.
    // @type {Boolean}

    _this2._transportReady = false; // Ids of alive local tracks.
    // @type {Set<Number>}

    _this2._trackIds = new Set();
    return _this2;
  }

  _createClass(SendHandler, [{
    key: "addProducer",
    value: function addProducer(producer) {
      var _this3 = this;

      var track = producer.track;
      logger.debug('addProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      if (this._trackIds.has(track.id)) return Promise.reject(new Error('track already added'));
      var transceiver;
      var localSdpObj; // Add the track id to the Set.

      this._trackIds.add(track.id);

      return Promise.resolve().then(function () {
        // Let's check if there is any inactive transceiver for same kind and
        // reuse it if so.
        transceiver = _this3._pc.getTransceivers().find(function (t) {
          return t.receiver.track.kind === track.kind && t.direction === 'inactive';
        });

        if (transceiver) {
          logger.debug('addProducer() | reusing an inactive transceiver');
          transceiver.direction = 'sendonly';
          return transceiver.sender.replaceTrack(track);
        } else {
          transceiver = _this3._pc.addTransceiver(track, {
            direction: 'sendonly'
          });
        }
      }).then(function () {
        return _this3._pc.createOffer();
      }).then(function (offer) {
        // If simulcast is set, mangle the offer.
        if (producer.simulcast) {
          logger.debug('addProducer() | enabling simulcast');

          var sdpObject = _sdpTransform.default.parse(offer.sdp);

          sdpUnifiedPlanUtils.addPlanBSimulcast(sdpObject, track, {
            mid: transceiver.mid
          });

          var offerSdp = _sdpTransform.default.write(sdpObject);

          offer = {
            type: 'offer',
            sdp: offerSdp
          };
        }

        logger.debug('addProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this3._pc.setLocalDescription(offer);
      }).then(function () {
        if (!_this3._transportReady) return _this3._setupTransport();
      }).then(function () {
        localSdpObj = _sdpTransform.default.parse(_this3._pc.localDescription.sdp);

        var remoteSdp = _this3._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('addProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this3._pc.setRemoteDescription(answer);
      }).then(function () {
        var rtpParameters = utils.clone(_this3._rtpParametersByKind[producer.kind]);
        sdpUnifiedPlanUtils.fillRtpParametersForTrack(rtpParameters, localSdpObj, track, {
          mid: transceiver.mid,
          planBSimulcast: true
        });
        return rtpParameters;
      }).catch(function (error) {
        // Panic here. Try to undo things.
        try {
          transceiver.direction = 'inactive';
        } catch (error2) {}

        _this3._trackIds.delete(track.id);

        throw error;
      });
    }
  }, {
    key: "removeProducer",
    value: function removeProducer(producer) {
      var _this4 = this;

      var track = producer.track;
      if (!this._trackIds.has(track.id)) return Promise.reject(new Error('track not found'));
      logger.debug('removeProducer() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      return Promise.resolve().then(function () {
        // Get the associated RTCRtpSender.
        var rtpSender = _this4._pc.getSenders().find(function (s) {
          return s.track === track;
        });

        if (!rtpSender) throw new Error('local track not found');

        _this4._pc.removeTrack(rtpSender); // Remove the track id from the Set.


        _this4._trackIds.delete(track.id);

        return _this4._pc.createOffer();
      }).then(function (offer) {
        logger.debug('removeProducer() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this4._pc.setLocalDescription(offer);
      }).then(function () {
        var localSdpObj = _sdpTransform.default.parse(_this4._pc.localDescription.sdp);

        var remoteSdp = _this4._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('removeProducer() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this4._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "replaceProducerTrack",
    value: function replaceProducerTrack(producer, track) {
      var _this5 = this;

      logger.debug('replaceProducerTrack() [id:%s, kind:%s, trackId:%s]', producer.id, producer.kind, track.id);
      var oldTrack = producer.track;
      return Promise.resolve().then(function () {
        // Get the associated RTCRtpSender.
        var rtpSender = _this5._pc.getSenders().find(function (s) {
          return s.track === oldTrack;
        });

        if (!rtpSender) throw new Error('local track not found');
        return rtpSender.replaceTrack(track);
      }).then(function () {
        // Remove the old track id from the Set.
        _this5._trackIds.delete(oldTrack.id); // Add the new track id to the Set.


        _this5._trackIds.add(track.id);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this6 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        return _this6._pc.createOffer({
          iceRestart: true
        });
      }).then(function (offer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
        return _this6._pc.setLocalDescription(offer);
      }).then(function () {
        var localSdpObj = _sdpTransform.default.parse(_this6._pc.localDescription.sdp);

        var remoteSdp = _this6._remoteSdp.createAnswerSdp(localSdpObj);

        var answer = {
          type: 'answer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
        return _this6._pc.setRemoteDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this7 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // Get our local DTLS parameters.
        var transportLocalParameters = {};
        var sdp = _this7._pc.localDescription.sdp;

        var sdpObj = _sdpTransform.default.parse(sdp);

        var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj); // Let's decide that we'll be DTLS server (because we can).

        dtlsParameters.role = 'server';
        transportLocalParameters.dtlsParameters = dtlsParameters; // Provide the remote SDP handler with transport local parameters.

        _this7._remoteSdp.setTransportLocalParameters(transportLocalParameters); // We need transport remote parameters.


        return _this7.safeEmitAsPromise('@needcreatetransport', transportLocalParameters);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this7._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this7._transportReady = true;
      });
    }
  }]);

  return SendHandler;
}(Handler);

var RecvHandler =
/*#__PURE__*/
function (_Handler2) {
  _inherits(RecvHandler, _Handler2);

  function RecvHandler(rtpParametersByKind, settings) {
    var _this8;

    _classCallCheck(this, RecvHandler);

    _this8 = _possibleConstructorReturn(this, _getPrototypeOf(RecvHandler).call(this, 'recv', rtpParametersByKind, settings)); // Got transport remote parameters.
    // @type {Boolean}

    _this8._transportCreated = false; // Got transport local parameters.
    // @type {Boolean}

    _this8._transportUpdated = false; // Map of Consumers information indexed by consumer.id.
    // - mid {String}
    // - kind {String}
    // - closed {Boolean}
    // - trackId {String}
    // - ssrc {Number}
    // - rtxSsrc {Number}
    // - cname {String}
    // @type {Map<Number, Object>}

    _this8._consumerInfos = new Map();
    return _this8;
  }

  _createClass(RecvHandler, [{
    key: "addConsumer",
    value: function addConsumer(consumer) {
      var _this9 = this;

      logger.debug('addConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);
      if (this._consumerInfos.has(consumer.id)) return Promise.reject(new Error('Consumer already added'));
      var encoding = consumer.rtpParameters.encodings[0];
      var cname = consumer.rtpParameters.rtcp.cname;
      var consumerInfo = {
        mid: "".concat(consumer.kind[0]).concat(consumer.id),
        kind: consumer.kind,
        closed: consumer.closed,
        streamId: "recv-stream-".concat(consumer.id),
        trackId: "consumer-".concat(consumer.kind, "-").concat(consumer.id),
        ssrc: encoding.ssrc,
        cname: cname
      };
      if (encoding.rtx && encoding.rtx.ssrc) consumerInfo.rtxSsrc = encoding.rtx.ssrc;

      this._consumerInfos.set(consumer.id, consumerInfo);

      return Promise.resolve().then(function () {
        if (!_this9._transportCreated) return _this9._setupTransport();
      }).then(function () {
        var remoteSdp = _this9._remoteSdp.createOfferSdp(Array.from(_this9._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('addConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this9._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this9._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('addConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this9._pc.setLocalDescription(answer);
      }).then(function () {
        if (!_this9._transportUpdated) return _this9._updateTransport();
      }).then(function () {
        var transceiver = _this9._pc.getTransceivers().find(function (t) {
          return t.mid === consumerInfo.mid;
        });

        if (!transceiver) throw new Error('remote track not found');
        return transceiver.receiver.track;
      });
    }
  }, {
    key: "removeConsumer",
    value: function removeConsumer(consumer) {
      var _this10 = this;

      logger.debug('removeConsumer() [id:%s, kind:%s]', consumer.id, consumer.kind);

      var consumerInfo = this._consumerInfos.get(consumer.id);

      if (!consumerInfo) return Promise.reject(new Error('Consumer not found'));
      consumerInfo.closed = true;
      return Promise.resolve().then(function () {
        var remoteSdp = _this10._remoteSdp.createOfferSdp(Array.from(_this10._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('removeConsumer() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this10._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this10._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('removeConsumer() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this10._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "restartIce",
    value: function restartIce(remoteIceParameters) {
      var _this11 = this;

      logger.debug('restartIce()'); // Provide the remote SDP handler with new remote ICE parameters.

      this._remoteSdp.updateTransportRemoteIceParameters(remoteIceParameters);

      return Promise.resolve().then(function () {
        var remoteSdp = _this11._remoteSdp.createOfferSdp(Array.from(_this11._consumerInfos.values()));

        var offer = {
          type: 'offer',
          sdp: remoteSdp
        };
        logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
        return _this11._pc.setRemoteDescription(offer);
      }).then(function () {
        return _this11._pc.createAnswer();
      }).then(function (answer) {
        logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
        return _this11._pc.setLocalDescription(answer);
      });
    }
  }, {
    key: "_setupTransport",
    value: function _setupTransport() {
      var _this12 = this;

      logger.debug('_setupTransport()');
      return Promise.resolve().then(function () {
        // We need transport remote parameters.
        return _this12.safeEmitAsPromise('@needcreatetransport', null);
      }).then(function (transportRemoteParameters) {
        // Provide the remote SDP handler with transport remote parameters.
        _this12._remoteSdp.setTransportRemoteParameters(transportRemoteParameters);

        _this12._transportCreated = true;
      });
    }
  }, {
    key: "_updateTransport",
    value: function _updateTransport() {
      logger.debug('_updateTransport()'); // Get our local DTLS parameters.

      var sdp = this._pc.localDescription.sdp;

      var sdpObj = _sdpTransform.default.parse(sdp);

      var dtlsParameters = sdpCommonUtils.extractDtlsParameters(sdpObj);
      var transportLocalParameters = {
        dtlsParameters: dtlsParameters
      }; // We need to provide transport local parameters.

      this.safeEmit('@needupdatetransport', transportLocalParameters);
      this._transportUpdated = true;
    }
  }]);

  return RecvHandler;
}(Handler);

var Safari12 =
/*#__PURE__*/
function () {
  _createClass(Safari12, null, [{
    key: "getNativeRtpCapabilities",
    value: function getNativeRtpCapabilities() {
      logger.debug('getNativeRtpCapabilities()');
      var pc = new RTCPeerConnection({
        iceServers: [],
        iceTransportPolicy: 'all',
        bundlePolicy: 'max-bundle',
        rtcpMuxPolicy: 'require'
      });
      pc.addTransceiver('audio');
      pc.addTransceiver('video');
      return pc.createOffer().then(function (offer) {
        try {
          pc.close();
        } catch (error) {}

        var sdpObj = _sdpTransform.default.parse(offer.sdp);

        var nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities(sdpObj);
        return nativeRtpCapabilities;
      }).catch(function (error) {
        try {
          pc.close();
        } catch (error2) {}

        throw error;
      });
    }
  }, {
    key: "tag",
    get: function get() {
      return 'Safari12';
    }
  }]);

  function Safari12(direction, extendedRtpCapabilities, settings) {
    _classCallCheck(this, Safari12);

    logger.debug('constructor() [direction:%s, extendedRtpCapabilities:%o]', direction, extendedRtpCapabilities);
    var rtpParametersByKind;

    switch (direction) {
      case 'send':
        {
          rtpParametersByKind = {
            audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
          };
          return new SendHandler(rtpParametersByKind, settings);
        }

      case 'recv':
        {
          rtpParametersByKind = {
            audio: ortc.getReceivingFullRtpParameters('audio', extendedRtpCapabilities),
            video: ortc.getReceivingFullRtpParameters('video', extendedRtpCapabilities)
          };
          return new RecvHandler(rtpParametersByKind, settings);
        }
    }
  }

  return Safari12;
}();

exports.default = Safari12;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/ortc/edgeUtils.js":
/*!**************************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/ortc/edgeUtils.js ***!
  \**************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.getCapabilities = getCapabilities;
exports.mangleRtpParameters = mangleRtpParameters;

var utils = _interopRequireWildcard(__webpack_require__(/*! ../../utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

/* global RTCRtpReceiver */

/**
 * Normalize Edge's RTCRtpReceiver.getCapabilities() to produce a full
 * compliant ORTC RTCRtpCapabilities.
 *
 * @return {RTCRtpCapabilities}
 */
function getCapabilities() {
  var nativeCaps = RTCRtpReceiver.getCapabilities();
  var caps = utils.clone(nativeCaps);
  var _iteratorNormalCompletion = true;
  var _didIteratorError = false;
  var _iteratorError = undefined;

  try {
    for (var _iterator = caps.codecs[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
      var codec = _step.value;
      // Rename numChannels to channels.
      codec.channels = codec.numChannels;
      delete codec.numChannels; // Normalize channels.

      if (codec.kind !== 'audio') delete codec.channels;else if (!codec.channels) codec.channels = 1; // Add mimeType.

      codec.mimeType = "".concat(codec.kind, "/").concat(codec.name); // NOTE: Edge sets some numeric parameters as String rather than Number. Fix them.

      if (codec.parameters) {
        var parameters = codec.parameters;
        if (parameters.apt) parameters.apt = Number(parameters.apt);
        if (parameters['packetization-mode']) parameters['packetization-mode'] = Number(parameters['packetization-mode']);
      } // Delete emty parameter String in rtcpFeedback.


      var _iteratorNormalCompletion2 = true;
      var _didIteratorError2 = false;
      var _iteratorError2 = undefined;

      try {
        for (var _iterator2 = (codec.rtcpFeedback || [])[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
          var feedback = _step2.value;
          if (!feedback.parameter) delete feedback.parameter;
        }
      } catch (err) {
        _didIteratorError2 = true;
        _iteratorError2 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion2 && _iterator2.return != null) {
            _iterator2.return();
          }
        } finally {
          if (_didIteratorError2) {
            throw _iteratorError2;
          }
        }
      }
    }
  } catch (err) {
    _didIteratorError = true;
    _iteratorError = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion && _iterator.return != null) {
        _iterator.return();
      }
    } finally {
      if (_didIteratorError) {
        throw _iteratorError;
      }
    }
  }

  return caps;
}
/**
 * Generate RTCRtpParameters as Edge like them.
 *
 * @param  {RTCRtpParameters} rtpParameters
 * @return {RTCRtpParameters}
 */


function mangleRtpParameters(rtpParameters) {
  var params = utils.clone(rtpParameters);
  var _iteratorNormalCompletion3 = true;
  var _didIteratorError3 = false;
  var _iteratorError3 = undefined;

  try {
    for (var _iterator3 = params.codecs[Symbol.iterator](), _step3; !(_iteratorNormalCompletion3 = (_step3 = _iterator3.next()).done); _iteratorNormalCompletion3 = true) {
      var codec = _step3.value;

      // Rename channels to numChannels.
      if (codec.channels) {
        codec.numChannels = codec.channels;
        delete codec.channels;
      } // Remove mimeType.


      delete codec.mimeType;
    }
  } catch (err) {
    _didIteratorError3 = true;
    _iteratorError3 = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion3 && _iterator3.return != null) {
        _iterator3.return();
      }
    } finally {
      if (_didIteratorError3) {
        throw _iteratorError3;
      }
    }
  }

  return params;
}

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemotePlainRtpSdp.js":
/*!*********************************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemotePlainRtpSdp.js ***!
  \*********************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _sdpTransform = _interopRequireDefault(__webpack_require__(/*! sdp-transform */ "./node_modules/sdp-transform/lib/index.js"));

var _Logger = _interopRequireDefault(__webpack_require__(/*! ../../Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var utils = _interopRequireWildcard(__webpack_require__(/*! ../../utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

var logger = new _Logger.default('RemotePlainRtpSdp');

var RemoteSdp =
/*#__PURE__*/
function () {
  function RemoteSdp(rtpParametersByKind) {
    _classCallCheck(this, RemoteSdp);

    // Generic sending RTP parameters for audio and video.
    // @type {Object}
    this._rtpParametersByKind = rtpParametersByKind; // Transport local parameters, including plain RTP parameteres.
    // @type {Object}

    this._transportLocalParameters = null; // Transport remote parameters, including plain RTP parameters.
    // @type {Object}

    this._transportRemoteParameters = null; // SDP global fields.
    // @type {Object}

    this._sdpGlobalFields = {
      id: utils.randomNumber(),
      version: 0
    };
  }

  _createClass(RemoteSdp, [{
    key: "setTransportLocalParameters",
    value: function setTransportLocalParameters(transportLocalParameters) {
      logger.debug('setTransportLocalParameters() [transportLocalParameters:%o]', transportLocalParameters);
      this._transportLocalParameters = transportLocalParameters;
    }
  }, {
    key: "setTransportRemoteParameters",
    value: function setTransportRemoteParameters(transportRemoteParameters) {
      logger.debug('setTransportRemoteParameters() [transportRemoteParameters:%o]', transportRemoteParameters);
      this._transportRemoteParameters = transportRemoteParameters;
    }
  }]);

  return RemoteSdp;
}();

var SendRemoteSdp =
/*#__PURE__*/
function (_RemoteSdp) {
  _inherits(SendRemoteSdp, _RemoteSdp);

  function SendRemoteSdp(rtpParametersByKind) {
    _classCallCheck(this, SendRemoteSdp);

    return _possibleConstructorReturn(this, _getPrototypeOf(SendRemoteSdp).call(this, rtpParametersByKind));
  }

  _createClass(SendRemoteSdp, [{
    key: "createAnswerSdp",
    value: function createAnswerSdp(localSdpObj) {
      logger.debug('createAnswerSdp()');
      if (!this._transportLocalParameters) throw new Error('no transport local parameters');else if (!this._transportRemoteParameters) throw new Error('no transport remote parameters');
      var remotePlainRtpParameters = this._transportRemoteParameters.plainRtpParameters;
      var sdpObj = {};
      var mids = (localSdpObj.media || []).filter(function (m) {
        return m.hasOwnProperty('mid');
      }).map(function (m) {
        return String(m.mid);
      }); // Increase our SDP version.

      this._sdpGlobalFields.version++;
      sdpObj.version = 0;
      sdpObj.origin = {
        address: remotePlainRtpParameters.ip,
        ipVer: remotePlainRtpParameters.version,
        netType: 'IN',
        sessionId: this._sdpGlobalFields.id,
        sessionVersion: this._sdpGlobalFields.version,
        username: 'mediasoup-client'
      };
      sdpObj.name = '-';
      sdpObj.timing = {
        start: 0,
        stop: 0
      };
      sdpObj.msidSemantic = {
        semantic: 'WMS',
        token: '*'
      };

      if (mids.length > 0) {
        sdpObj.groups = [{
          type: 'BUNDLE',
          mids: mids.join(' ')
        }];
      }

      sdpObj.media = [];
      var _iteratorNormalCompletion = true;
      var _didIteratorError = false;
      var _iteratorError = undefined;

      try {
        for (var _iterator = (localSdpObj.media || [])[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
          var localMediaObj = _step.value;
          var closed = localMediaObj.direction === 'inactive';
          var kind = localMediaObj.type;
          var codecs = this._rtpParametersByKind[kind].codecs;
          var headerExtensions = this._rtpParametersByKind[kind].headerExtensions;
          var remoteMediaObj = {};
          remoteMediaObj.type = localMediaObj.type;
          remoteMediaObj.port = remotePlainRtpParameters.port;
          remoteMediaObj.protocol = 'RTP/AVP';
          remoteMediaObj.connection = {
            ip: remotePlainRtpParameters.ip,
            version: remotePlainRtpParameters.version
          };
          remoteMediaObj.mid = localMediaObj.mid;

          switch (localMediaObj.direction) {
            case 'sendrecv':
            case 'sendonly':
              remoteMediaObj.direction = 'recvonly';
              break;

            case 'recvonly':
            case 'inactive':
              remoteMediaObj.direction = 'inactive';
              break;
          }

          remoteMediaObj.rtp = [];
          remoteMediaObj.rtcpFb = [];
          remoteMediaObj.fmtp = [];
          var _iteratorNormalCompletion2 = true;
          var _didIteratorError2 = false;
          var _iteratorError2 = undefined;

          try {
            for (var _iterator2 = codecs[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
              var codec = _step2.value;
              var rtp = {
                payload: codec.payloadType,
                codec: codec.name,
                rate: codec.clockRate
              };
              if (codec.channels > 1) rtp.encoding = codec.channels;
              remoteMediaObj.rtp.push(rtp);

              if (codec.parameters) {
                var paramFmtp = {
                  payload: codec.payloadType,
                  config: ''
                };

                var _arr = Object.keys(codec.parameters);

                for (var _i = 0; _i < _arr.length; _i++) {
                  var key = _arr[_i];
                  if (paramFmtp.config) paramFmtp.config += ';';
                  paramFmtp.config += "".concat(key, "=").concat(codec.parameters[key]);
                }

                if (paramFmtp.config) remoteMediaObj.fmtp.push(paramFmtp);
              }

              if (codec.rtcpFeedback) {
                var _iteratorNormalCompletion5 = true;
                var _didIteratorError5 = false;
                var _iteratorError5 = undefined;

                try {
                  for (var _iterator5 = codec.rtcpFeedback[Symbol.iterator](), _step5; !(_iteratorNormalCompletion5 = (_step5 = _iterator5.next()).done); _iteratorNormalCompletion5 = true) {
                    var fb = _step5.value;
                    remoteMediaObj.rtcpFb.push({
                      payload: codec.payloadType,
                      type: fb.type,
                      subtype: fb.parameter || ''
                    });
                  }
                } catch (err) {
                  _didIteratorError5 = true;
                  _iteratorError5 = err;
                } finally {
                  try {
                    if (!_iteratorNormalCompletion5 && _iterator5.return != null) {
                      _iterator5.return();
                    }
                  } finally {
                    if (_didIteratorError5) {
                      throw _iteratorError5;
                    }
                  }
                }
              }
            }
          } catch (err) {
            _didIteratorError2 = true;
            _iteratorError2 = err;
          } finally {
            try {
              if (!_iteratorNormalCompletion2 && _iterator2.return != null) {
                _iterator2.return();
              }
            } finally {
              if (_didIteratorError2) {
                throw _iteratorError2;
              }
            }
          }

          remoteMediaObj.payloads = codecs.map(function (codec) {
            return codec.payloadType;
          }).join(' ');

          if (!closed) {
            remoteMediaObj.ext = [];
            var _iteratorNormalCompletion3 = true;
            var _didIteratorError3 = false;
            var _iteratorError3 = undefined;

            try {
              var _loop = function _loop() {
                var ext = _step3.value;
                // Don't add a header extension if not present in the offer.
                var matchedLocalExt = (localMediaObj.ext || []).find(function (localExt) {
                  return localExt.uri === ext.uri;
                });
                if (!matchedLocalExt) return "continue";
                remoteMediaObj.ext.push({
                  uri: ext.uri,
                  value: ext.id
                });
              };

              for (var _iterator3 = headerExtensions[Symbol.iterator](), _step3; !(_iteratorNormalCompletion3 = (_step3 = _iterator3.next()).done); _iteratorNormalCompletion3 = true) {
                var _ret = _loop();

                if (_ret === "continue") continue;
              }
            } catch (err) {
              _didIteratorError3 = true;
              _iteratorError3 = err;
            } finally {
              try {
                if (!_iteratorNormalCompletion3 && _iterator3.return != null) {
                  _iterator3.return();
                }
              } finally {
                if (_didIteratorError3) {
                  throw _iteratorError3;
                }
              }
            }
          } // Simulcast.


          if (localMediaObj.simulcast_03) {
            // eslint-disable-next-line camelcase
            remoteMediaObj.simulcast_03 = {
              value: localMediaObj.simulcast_03.value.replace(/send/g, 'recv')
            };
            remoteMediaObj.rids = [];
            var _iteratorNormalCompletion4 = true;
            var _didIteratorError4 = false;
            var _iteratorError4 = undefined;

            try {
              for (var _iterator4 = (localMediaObj.rids || [])[Symbol.iterator](), _step4; !(_iteratorNormalCompletion4 = (_step4 = _iterator4.next()).done); _iteratorNormalCompletion4 = true) {
                var rid = _step4.value;
                if (rid.direction !== 'send') continue;
                remoteMediaObj.rids.push({
                  id: rid.id,
                  direction: 'recv'
                });
              }
            } catch (err) {
              _didIteratorError4 = true;
              _iteratorError4 = err;
            } finally {
              try {
                if (!_iteratorNormalCompletion4 && _iterator4.return != null) {
                  _iterator4.return();
                }
              } finally {
                if (_didIteratorError4) {
                  throw _iteratorError4;
                }
              }
            }
          }

          remoteMediaObj.rtcpMux = 'rtcp-mux';
          remoteMediaObj.rtcpRsize = 'rtcp-rsize'; // Push it.

          sdpObj.media.push(remoteMediaObj);
        }
      } catch (err) {
        _didIteratorError = true;
        _iteratorError = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion && _iterator.return != null) {
            _iterator.return();
          }
        } finally {
          if (_didIteratorError) {
            throw _iteratorError;
          }
        }
      }

      var sdp = _sdpTransform.default.write(sdpObj);

      return sdp;
    }
  }]);

  return SendRemoteSdp;
}(RemoteSdp);

var RecvRemoteSdp =
/*#__PURE__*/
function (_RemoteSdp2) {
  _inherits(RecvRemoteSdp, _RemoteSdp2);

  function RecvRemoteSdp(rtpParametersByKind) {
    _classCallCheck(this, RecvRemoteSdp);

    return _possibleConstructorReturn(this, _getPrototypeOf(RecvRemoteSdp).call(this, rtpParametersByKind));
  }
  /**
   * @param {Array<Object>} consumerInfos - Consumer informations.
   * @return {String}
   */


  _createClass(RecvRemoteSdp, [{
    key: "createOfferSdp",
    value: function createOfferSdp(consumerInfos) {
      logger.debug('createOfferSdp()');
      if (!this._transportRemoteParameters) throw new Error('no transport remote parameters');
      var remotePlainRtpParameters = this._transportRemoteParameters.plainRtpParameters;
      var sdpObj = {};
      var mids = consumerInfos.map(function (info) {
        return String(info.mid);
      }); // Increase our SDP version.

      this._sdpGlobalFields.version++;
      sdpObj.version = 0;
      sdpObj.origin = {
        address: remotePlainRtpParameters.ip,
        ipVer: remotePlainRtpParameters.version,
        netType: 'IN',
        sessionId: this._sdpGlobalFields.id,
        sessionVersion: this._sdpGlobalFields.version,
        username: 'mediasoup-client'
      };
      sdpObj.name = '-';
      sdpObj.timing = {
        start: 0,
        stop: 0
      };
      sdpObj.msidSemantic = {
        semantic: 'WMS',
        token: '*'
      };

      if (mids.length > 0) {
        sdpObj.groups = [{
          type: 'BUNDLE',
          mids: mids.join(' ')
        }];
      }

      sdpObj.media = [];
      var _iteratorNormalCompletion6 = true;
      var _didIteratorError6 = false;
      var _iteratorError6 = undefined;

      try {
        for (var _iterator6 = consumerInfos[Symbol.iterator](), _step6; !(_iteratorNormalCompletion6 = (_step6 = _iterator6.next()).done); _iteratorNormalCompletion6 = true) {
          var info = _step6.value;
          var closed = info.closed;
          var kind = info.kind;
          var codecs = this._rtpParametersByKind[kind].codecs;
          var headerExtensions = this._rtpParametersByKind[kind].headerExtensions;
          var remoteMediaObj = {};
          remoteMediaObj.type = kind;
          remoteMediaObj.mid = info.mid;
          remoteMediaObj.msid = "".concat(info.streamId, " ").concat(info.trackId);
          remoteMediaObj.port = remotePlainRtpParameters.port;
          remoteMediaObj.protocol = 'RTP/AVP';
          remoteMediaObj.connection = {
            ip: remotePlainRtpParameters.ip,
            version: remotePlainRtpParameters.version
          };
          if (!closed) remoteMediaObj.direction = 'sendonly';else remoteMediaObj.direction = 'inactive';
          remoteMediaObj.rtp = [];
          remoteMediaObj.rtcpFb = [];
          remoteMediaObj.fmtp = [];
          var _iteratorNormalCompletion7 = true;
          var _didIteratorError7 = false;
          var _iteratorError7 = undefined;

          try {
            for (var _iterator7 = codecs[Symbol.iterator](), _step7; !(_iteratorNormalCompletion7 = (_step7 = _iterator7.next()).done); _iteratorNormalCompletion7 = true) {
              var codec = _step7.value;
              var rtp = {
                payload: codec.payloadType,
                codec: codec.name,
                rate: codec.clockRate
              };
              if (codec.channels > 1) rtp.encoding = codec.channels;
              remoteMediaObj.rtp.push(rtp);

              if (codec.parameters) {
                var paramFmtp = {
                  payload: codec.payloadType,
                  config: ''
                };

                var _arr2 = Object.keys(codec.parameters);

                for (var _i2 = 0; _i2 < _arr2.length; _i2++) {
                  var key = _arr2[_i2];
                  if (paramFmtp.config) paramFmtp.config += ';';
                  paramFmtp.config += "".concat(key, "=").concat(codec.parameters[key]);
                }

                if (paramFmtp.config) remoteMediaObj.fmtp.push(paramFmtp);
              }

              if (codec.rtcpFeedback) {
                var _iteratorNormalCompletion9 = true;
                var _didIteratorError9 = false;
                var _iteratorError9 = undefined;

                try {
                  for (var _iterator9 = codec.rtcpFeedback[Symbol.iterator](), _step9; !(_iteratorNormalCompletion9 = (_step9 = _iterator9.next()).done); _iteratorNormalCompletion9 = true) {
                    var fb = _step9.value;
                    remoteMediaObj.rtcpFb.push({
                      payload: codec.payloadType,
                      type: fb.type,
                      subtype: fb.parameter || ''
                    });
                  }
                } catch (err) {
                  _didIteratorError9 = true;
                  _iteratorError9 = err;
                } finally {
                  try {
                    if (!_iteratorNormalCompletion9 && _iterator9.return != null) {
                      _iterator9.return();
                    }
                  } finally {
                    if (_didIteratorError9) {
                      throw _iteratorError9;
                    }
                  }
                }
              }
            }
          } catch (err) {
            _didIteratorError7 = true;
            _iteratorError7 = err;
          } finally {
            try {
              if (!_iteratorNormalCompletion7 && _iterator7.return != null) {
                _iterator7.return();
              }
            } finally {
              if (_didIteratorError7) {
                throw _iteratorError7;
              }
            }
          }

          remoteMediaObj.payloads = codecs.map(function (codec) {
            return codec.payloadType;
          }).join(' ');

          if (!closed) {
            remoteMediaObj.ext = [];
            var _iteratorNormalCompletion8 = true;
            var _didIteratorError8 = false;
            var _iteratorError8 = undefined;

            try {
              for (var _iterator8 = headerExtensions[Symbol.iterator](), _step8; !(_iteratorNormalCompletion8 = (_step8 = _iterator8.next()).done); _iteratorNormalCompletion8 = true) {
                var ext = _step8.value;
                // Ignore MID RTP extension for receiving media.
                if (ext.uri === 'urn:ietf:params:rtp-hdrext:sdes:mid') continue;
                remoteMediaObj.ext.push({
                  uri: ext.uri,
                  value: ext.id
                });
              }
            } catch (err) {
              _didIteratorError8 = true;
              _iteratorError8 = err;
            } finally {
              try {
                if (!_iteratorNormalCompletion8 && _iterator8.return != null) {
                  _iterator8.return();
                }
              } finally {
                if (_didIteratorError8) {
                  throw _iteratorError8;
                }
              }
            }
          }

          remoteMediaObj.rtcpMux = 'rtcp-mux';
          remoteMediaObj.rtcpRsize = 'rtcp-rsize';

          if (!closed) {
            remoteMediaObj.ssrcs = [];
            remoteMediaObj.ssrcGroups = [];
            remoteMediaObj.ssrcs.push({
              id: info.ssrc,
              attribute: 'cname',
              value: info.cname
            });

            if (info.rtxSsrc) {
              remoteMediaObj.ssrcs.push({
                id: info.rtxSsrc,
                attribute: 'cname',
                value: info.cname
              }); // Associate original and retransmission SSRC.

              remoteMediaObj.ssrcGroups.push({
                semantics: 'FID',
                ssrcs: "".concat(info.ssrc, " ").concat(info.rtxSsrc)
              });
            }
          } // Push it.


          sdpObj.media.push(remoteMediaObj);
        }
      } catch (err) {
        _didIteratorError6 = true;
        _iteratorError6 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion6 && _iterator6.return != null) {
            _iterator6.return();
          }
        } finally {
          if (_didIteratorError6) {
            throw _iteratorError6;
          }
        }
      }

      var sdp = _sdpTransform.default.write(sdpObj);

      return sdp;
    }
  }]);

  return RecvRemoteSdp;
}(RemoteSdp);

var RemotePlainRtpSdp = function RemotePlainRtpSdp(direction, rtpParametersByKind) {
  _classCallCheck(this, RemotePlainRtpSdp);

  logger.debug('constructor() [direction:%s, rtpParametersByKind:%o]', direction, rtpParametersByKind);

  switch (direction) {
    case 'send':
      return new SendRemoteSdp(rtpParametersByKind);

    case 'recv':
      return new RecvRemoteSdp(rtpParametersByKind);
  }
};

exports.default = RemotePlainRtpSdp;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemotePlanBSdp.js":
/*!******************************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemotePlanBSdp.js ***!
  \******************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _sdpTransform = _interopRequireDefault(__webpack_require__(/*! sdp-transform */ "./node_modules/sdp-transform/lib/index.js"));

var _Logger = _interopRequireDefault(__webpack_require__(/*! ../../Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var utils = _interopRequireWildcard(__webpack_require__(/*! ../../utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

var logger = new _Logger.default('RemotePlanBSdp');

var RemoteSdp =
/*#__PURE__*/
function () {
  function RemoteSdp(rtpParametersByKind) {
    _classCallCheck(this, RemoteSdp);

    // Generic sending RTP parameters for audio and video.
    // @type {Object}
    this._rtpParametersByKind = rtpParametersByKind; // Transport local parameters, including DTLS parameteres.
    // @type {Object}

    this._transportLocalParameters = null; // Transport remote parameters, including ICE parameters, ICE candidates
    // and DTLS parameteres.
    // @type {Object}

    this._transportRemoteParameters = null; // SDP global fields.
    // @type {Object}

    this._sdpGlobalFields = {
      id: utils.randomNumber(),
      version: 0
    };
  }

  _createClass(RemoteSdp, [{
    key: "setTransportLocalParameters",
    value: function setTransportLocalParameters(transportLocalParameters) {
      logger.debug('setTransportLocalParameters() [transportLocalParameters:%o]', transportLocalParameters);
      this._transportLocalParameters = transportLocalParameters;
    }
  }, {
    key: "setTransportRemoteParameters",
    value: function setTransportRemoteParameters(transportRemoteParameters) {
      logger.debug('setTransportRemoteParameters() [transportRemoteParameters:%o]', transportRemoteParameters);
      this._transportRemoteParameters = transportRemoteParameters;
    }
  }, {
    key: "updateTransportRemoteIceParameters",
    value: function updateTransportRemoteIceParameters(remoteIceParameters) {
      logger.debug('updateTransportRemoteIceParameters() [remoteIceParameters:%o]', remoteIceParameters);
      this._transportRemoteParameters.iceParameters = remoteIceParameters;
    }
  }]);

  return RemoteSdp;
}();

var SendRemoteSdp =
/*#__PURE__*/
function (_RemoteSdp) {
  _inherits(SendRemoteSdp, _RemoteSdp);

  function SendRemoteSdp(rtpParametersByKind) {
    _classCallCheck(this, SendRemoteSdp);

    return _possibleConstructorReturn(this, _getPrototypeOf(SendRemoteSdp).call(this, rtpParametersByKind));
  }

  _createClass(SendRemoteSdp, [{
    key: "createAnswerSdp",
    value: function createAnswerSdp(localSdpObj) {
      logger.debug('createAnswerSdp()');
      if (!this._transportLocalParameters) throw new Error('no transport local parameters');else if (!this._transportRemoteParameters) throw new Error('no transport remote parameters');
      var remoteIceParameters = this._transportRemoteParameters.iceParameters;
      var remoteIceCandidates = this._transportRemoteParameters.iceCandidates;
      var remoteDtlsParameters = this._transportRemoteParameters.dtlsParameters;
      var sdpObj = {};
      var mids = (localSdpObj.media || []).map(function (m) {
        return String(m.mid);
      }); // Increase our SDP version.

      this._sdpGlobalFields.version++;
      sdpObj.version = 0;
      sdpObj.origin = {
        address: '0.0.0.0',
        ipVer: 4,
        netType: 'IN',
        sessionId: this._sdpGlobalFields.id,
        sessionVersion: this._sdpGlobalFields.version,
        username: 'mediasoup-client'
      };
      sdpObj.name = '-';
      sdpObj.timing = {
        start: 0,
        stop: 0
      };
      sdpObj.icelite = remoteIceParameters.iceLite ? 'ice-lite' : null;
      sdpObj.msidSemantic = {
        semantic: 'WMS',
        token: '*'
      };
      sdpObj.groups = [{
        type: 'BUNDLE',
        mids: mids.join(' ')
      }];
      sdpObj.media = []; // NOTE: We take the latest fingerprint.

      var numFingerprints = remoteDtlsParameters.fingerprints.length;
      sdpObj.fingerprint = {
        type: remoteDtlsParameters.fingerprints[numFingerprints - 1].algorithm,
        hash: remoteDtlsParameters.fingerprints[numFingerprints - 1].value
      };
      var _iteratorNormalCompletion = true;
      var _didIteratorError = false;
      var _iteratorError = undefined;

      try {
        for (var _iterator = (localSdpObj.media || [])[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
          var localMediaObj = _step.value;
          var kind = localMediaObj.type;
          var codecs = this._rtpParametersByKind[kind].codecs;
          var headerExtensions = this._rtpParametersByKind[kind].headerExtensions;
          var remoteMediaObj = {};
          remoteMediaObj.type = localMediaObj.type;
          remoteMediaObj.port = 7;
          remoteMediaObj.protocol = 'RTP/SAVPF';
          remoteMediaObj.connection = {
            ip: '127.0.0.1',
            version: 4
          };
          remoteMediaObj.mid = localMediaObj.mid;
          remoteMediaObj.iceUfrag = remoteIceParameters.usernameFragment;
          remoteMediaObj.icePwd = remoteIceParameters.password;
          remoteMediaObj.candidates = [];
          var _iteratorNormalCompletion2 = true;
          var _didIteratorError2 = false;
          var _iteratorError2 = undefined;

          try {
            for (var _iterator2 = remoteIceCandidates[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
              var candidate = _step2.value;
              var candidateObj = {}; // mediasoup does not support non rtcp-mux so candidates component is
              // always RTP (1).

              candidateObj.component = 1;
              candidateObj.foundation = candidate.foundation;
              candidateObj.ip = candidate.ip;
              candidateObj.port = candidate.port;
              candidateObj.priority = candidate.priority;
              candidateObj.transport = candidate.protocol;
              candidateObj.type = candidate.type;
              if (candidate.tcpType) candidateObj.tcptype = candidate.tcpType;
              remoteMediaObj.candidates.push(candidateObj);
            }
          } catch (err) {
            _didIteratorError2 = true;
            _iteratorError2 = err;
          } finally {
            try {
              if (!_iteratorNormalCompletion2 && _iterator2.return != null) {
                _iterator2.return();
              }
            } finally {
              if (_didIteratorError2) {
                throw _iteratorError2;
              }
            }
          }

          remoteMediaObj.endOfCandidates = 'end-of-candidates'; // Announce support for ICE renomination.
          // https://tools.ietf.org/html/draft-thatcher-ice-renomination

          remoteMediaObj.iceOptions = 'renomination';

          switch (remoteDtlsParameters.role) {
            case 'client':
              remoteMediaObj.setup = 'active';
              break;

            case 'server':
              remoteMediaObj.setup = 'passive';
              break;
          }

          switch (localMediaObj.direction) {
            case 'sendrecv':
            case 'sendonly':
              remoteMediaObj.direction = 'recvonly';
              break;

            case 'recvonly':
            case 'inactive':
              remoteMediaObj.direction = 'inactive';
              break;
          } // If video, be ready for simulcast.


          if (kind === 'video') remoteMediaObj.xGoogleFlag = 'conference';
          remoteMediaObj.rtp = [];
          remoteMediaObj.rtcpFb = [];
          remoteMediaObj.fmtp = [];
          var _iteratorNormalCompletion3 = true;
          var _didIteratorError3 = false;
          var _iteratorError3 = undefined;

          try {
            for (var _iterator3 = codecs[Symbol.iterator](), _step3; !(_iteratorNormalCompletion3 = (_step3 = _iterator3.next()).done); _iteratorNormalCompletion3 = true) {
              var codec = _step3.value;
              var rtp = {
                payload: codec.payloadType,
                codec: codec.name,
                rate: codec.clockRate
              };
              if (codec.channels > 1) rtp.encoding = codec.channels;
              remoteMediaObj.rtp.push(rtp);

              if (codec.parameters) {
                var paramFmtp = {
                  payload: codec.payloadType,
                  config: ''
                };

                var _arr = Object.keys(codec.parameters);

                for (var _i = 0; _i < _arr.length; _i++) {
                  var key = _arr[_i];
                  if (paramFmtp.config) paramFmtp.config += ';';
                  paramFmtp.config += "".concat(key, "=").concat(codec.parameters[key]);
                }

                if (paramFmtp.config) remoteMediaObj.fmtp.push(paramFmtp);
              }

              if (codec.rtcpFeedback) {
                var _iteratorNormalCompletion5 = true;
                var _didIteratorError5 = false;
                var _iteratorError5 = undefined;

                try {
                  for (var _iterator5 = codec.rtcpFeedback[Symbol.iterator](), _step5; !(_iteratorNormalCompletion5 = (_step5 = _iterator5.next()).done); _iteratorNormalCompletion5 = true) {
                    var fb = _step5.value;
                    remoteMediaObj.rtcpFb.push({
                      payload: codec.payloadType,
                      type: fb.type,
                      subtype: fb.parameter || ''
                    });
                  }
                } catch (err) {
                  _didIteratorError5 = true;
                  _iteratorError5 = err;
                } finally {
                  try {
                    if (!_iteratorNormalCompletion5 && _iterator5.return != null) {
                      _iterator5.return();
                    }
                  } finally {
                    if (_didIteratorError5) {
                      throw _iteratorError5;
                    }
                  }
                }
              }
            }
          } catch (err) {
            _didIteratorError3 = true;
            _iteratorError3 = err;
          } finally {
            try {
              if (!_iteratorNormalCompletion3 && _iterator3.return != null) {
                _iterator3.return();
              }
            } finally {
              if (_didIteratorError3) {
                throw _iteratorError3;
              }
            }
          }

          remoteMediaObj.payloads = codecs.map(function (codec) {
            return codec.payloadType;
          }).join(' ');
          remoteMediaObj.ext = [];
          var _iteratorNormalCompletion4 = true;
          var _didIteratorError4 = false;
          var _iteratorError4 = undefined;

          try {
            var _loop = function _loop() {
              var ext = _step4.value;
              // Don't add a header extension if not present in the offer.
              var matchedLocalExt = (localMediaObj.ext || []).find(function (localExt) {
                return localExt.uri === ext.uri;
              });
              if (!matchedLocalExt) return "continue";
              remoteMediaObj.ext.push({
                uri: ext.uri,
                value: ext.id
              });
            };

            for (var _iterator4 = headerExtensions[Symbol.iterator](), _step4; !(_iteratorNormalCompletion4 = (_step4 = _iterator4.next()).done); _iteratorNormalCompletion4 = true) {
              var _ret = _loop();

              if (_ret === "continue") continue;
            }
          } catch (err) {
            _didIteratorError4 = true;
            _iteratorError4 = err;
          } finally {
            try {
              if (!_iteratorNormalCompletion4 && _iterator4.return != null) {
                _iterator4.return();
              }
            } finally {
              if (_didIteratorError4) {
                throw _iteratorError4;
              }
            }
          }

          remoteMediaObj.rtcpMux = 'rtcp-mux';
          remoteMediaObj.rtcpRsize = 'rtcp-rsize'; // Push it.

          sdpObj.media.push(remoteMediaObj);
        }
      } catch (err) {
        _didIteratorError = true;
        _iteratorError = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion && _iterator.return != null) {
            _iterator.return();
          }
        } finally {
          if (_didIteratorError) {
            throw _iteratorError;
          }
        }
      }

      var sdp = _sdpTransform.default.write(sdpObj);

      return sdp;
    }
  }]);

  return SendRemoteSdp;
}(RemoteSdp);

var RecvRemoteSdp =
/*#__PURE__*/
function (_RemoteSdp2) {
  _inherits(RecvRemoteSdp, _RemoteSdp2);

  function RecvRemoteSdp(rtpParametersByKind) {
    _classCallCheck(this, RecvRemoteSdp);

    return _possibleConstructorReturn(this, _getPrototypeOf(RecvRemoteSdp).call(this, rtpParametersByKind));
  }
  /**
   * @param {Array<String>} kinds - Media kinds.
   * @param {Array<Object>} consumerInfos - Consumer informations.
   * @return {String}
   */


  _createClass(RecvRemoteSdp, [{
    key: "createOfferSdp",
    value: function createOfferSdp(kinds, consumerInfos) {
      var _this = this;

      logger.debug('createOfferSdp()');
      if (!this._transportRemoteParameters) throw new Error('no transport remote parameters');
      var remoteIceParameters = this._transportRemoteParameters.iceParameters;
      var remoteIceCandidates = this._transportRemoteParameters.iceCandidates;
      var remoteDtlsParameters = this._transportRemoteParameters.dtlsParameters;
      var sdpObj = {};
      var mids = kinds; // Increase our SDP version.

      this._sdpGlobalFields.version++;
      sdpObj.version = 0;
      sdpObj.origin = {
        address: '0.0.0.0',
        ipVer: 4,
        netType: 'IN',
        sessionId: this._sdpGlobalFields.id,
        sessionVersion: this._sdpGlobalFields.version,
        username: 'mediasoup-client'
      };
      sdpObj.name = '-';
      sdpObj.timing = {
        start: 0,
        stop: 0
      };
      sdpObj.icelite = remoteIceParameters.iceLite ? 'ice-lite' : null;
      sdpObj.msidSemantic = {
        semantic: 'WMS',
        token: '*'
      };
      sdpObj.groups = [{
        type: 'BUNDLE',
        mids: mids.join(' ')
      }];
      sdpObj.media = []; // NOTE: We take the latest fingerprint.

      var numFingerprints = remoteDtlsParameters.fingerprints.length;
      sdpObj.fingerprint = {
        type: remoteDtlsParameters.fingerprints[numFingerprints - 1].algorithm,
        hash: remoteDtlsParameters.fingerprints[numFingerprints - 1].value
      };
      var _iteratorNormalCompletion6 = true;
      var _didIteratorError6 = false;
      var _iteratorError6 = undefined;

      try {
        var _loop2 = function _loop2() {
          var kind = _step6.value;
          var codecs = _this._rtpParametersByKind[kind].codecs;
          var headerExtensions = _this._rtpParametersByKind[kind].headerExtensions;
          var remoteMediaObj = {};
          remoteMediaObj.type = kind;
          remoteMediaObj.port = 7;
          remoteMediaObj.protocol = 'RTP/SAVPF';
          remoteMediaObj.connection = {
            ip: '127.0.0.1',
            version: 4
          };
          remoteMediaObj.mid = kind;
          remoteMediaObj.iceUfrag = remoteIceParameters.usernameFragment;
          remoteMediaObj.icePwd = remoteIceParameters.password;
          remoteMediaObj.candidates = [];
          var _iteratorNormalCompletion7 = true;
          var _didIteratorError7 = false;
          var _iteratorError7 = undefined;

          try {
            for (var _iterator7 = remoteIceCandidates[Symbol.iterator](), _step7; !(_iteratorNormalCompletion7 = (_step7 = _iterator7.next()).done); _iteratorNormalCompletion7 = true) {
              var candidate = _step7.value;
              var candidateObj = {}; // mediasoup does not support non rtcp-mux so candidates component is
              // always RTP (1).

              candidateObj.component = 1;
              candidateObj.foundation = candidate.foundation;
              candidateObj.ip = candidate.ip;
              candidateObj.port = candidate.port;
              candidateObj.priority = candidate.priority;
              candidateObj.transport = candidate.protocol;
              candidateObj.type = candidate.type;
              if (candidate.tcpType) candidateObj.tcptype = candidate.tcpType;
              remoteMediaObj.candidates.push(candidateObj);
            }
          } catch (err) {
            _didIteratorError7 = true;
            _iteratorError7 = err;
          } finally {
            try {
              if (!_iteratorNormalCompletion7 && _iterator7.return != null) {
                _iterator7.return();
              }
            } finally {
              if (_didIteratorError7) {
                throw _iteratorError7;
              }
            }
          }

          remoteMediaObj.endOfCandidates = 'end-of-candidates'; // Announce support for ICE renomination.
          // https://tools.ietf.org/html/draft-thatcher-ice-renomination

          remoteMediaObj.iceOptions = 'renomination';
          remoteMediaObj.setup = 'actpass';
          if (consumerInfos.some(function (info) {
            return info.kind === kind;
          })) remoteMediaObj.direction = 'sendonly';else remoteMediaObj.direction = 'inactive';
          remoteMediaObj.rtp = [];
          remoteMediaObj.rtcpFb = [];
          remoteMediaObj.fmtp = [];
          var _iteratorNormalCompletion8 = true;
          var _didIteratorError8 = false;
          var _iteratorError8 = undefined;

          try {
            for (var _iterator8 = codecs[Symbol.iterator](), _step8; !(_iteratorNormalCompletion8 = (_step8 = _iterator8.next()).done); _iteratorNormalCompletion8 = true) {
              var codec = _step8.value;
              var rtp = {
                payload: codec.payloadType,
                codec: codec.name,
                rate: codec.clockRate
              };
              if (codec.channels > 1) rtp.encoding = codec.channels;
              remoteMediaObj.rtp.push(rtp);

              if (codec.parameters) {
                var paramFmtp = {
                  payload: codec.payloadType,
                  config: ''
                };

                var _arr2 = Object.keys(codec.parameters);

                for (var _i2 = 0; _i2 < _arr2.length; _i2++) {
                  var key = _arr2[_i2];
                  if (paramFmtp.config) paramFmtp.config += ';';
                  paramFmtp.config += "".concat(key, "=").concat(codec.parameters[key]);
                }

                if (paramFmtp.config) remoteMediaObj.fmtp.push(paramFmtp);
              }

              if (codec.rtcpFeedback) {
                var _iteratorNormalCompletion11 = true;
                var _didIteratorError11 = false;
                var _iteratorError11 = undefined;

                try {
                  for (var _iterator11 = codec.rtcpFeedback[Symbol.iterator](), _step11; !(_iteratorNormalCompletion11 = (_step11 = _iterator11.next()).done); _iteratorNormalCompletion11 = true) {
                    var fb = _step11.value;
                    remoteMediaObj.rtcpFb.push({
                      payload: codec.payloadType,
                      type: fb.type,
                      subtype: fb.parameter || ''
                    });
                  }
                } catch (err) {
                  _didIteratorError11 = true;
                  _iteratorError11 = err;
                } finally {
                  try {
                    if (!_iteratorNormalCompletion11 && _iterator11.return != null) {
                      _iterator11.return();
                    }
                  } finally {
                    if (_didIteratorError11) {
                      throw _iteratorError11;
                    }
                  }
                }
              }
            }
          } catch (err) {
            _didIteratorError8 = true;
            _iteratorError8 = err;
          } finally {
            try {
              if (!_iteratorNormalCompletion8 && _iterator8.return != null) {
                _iterator8.return();
              }
            } finally {
              if (_didIteratorError8) {
                throw _iteratorError8;
              }
            }
          }

          remoteMediaObj.payloads = codecs.map(function (codec) {
            return codec.payloadType;
          }).join(' ');
          remoteMediaObj.ext = [];
          var _iteratorNormalCompletion9 = true;
          var _didIteratorError9 = false;
          var _iteratorError9 = undefined;

          try {
            for (var _iterator9 = headerExtensions[Symbol.iterator](), _step9; !(_iteratorNormalCompletion9 = (_step9 = _iterator9.next()).done); _iteratorNormalCompletion9 = true) {
              var ext = _step9.value;
              // Ignore MID RTP extension for receiving media.
              if (ext.uri === 'urn:ietf:params:rtp-hdrext:sdes:mid') continue;
              remoteMediaObj.ext.push({
                uri: ext.uri,
                value: ext.id
              });
            }
          } catch (err) {
            _didIteratorError9 = true;
            _iteratorError9 = err;
          } finally {
            try {
              if (!_iteratorNormalCompletion9 && _iterator9.return != null) {
                _iterator9.return();
              }
            } finally {
              if (_didIteratorError9) {
                throw _iteratorError9;
              }
            }
          }

          remoteMediaObj.rtcpMux = 'rtcp-mux';
          remoteMediaObj.rtcpRsize = 'rtcp-rsize';
          remoteMediaObj.ssrcs = [];
          remoteMediaObj.ssrcGroups = [];
          var _iteratorNormalCompletion10 = true;
          var _didIteratorError10 = false;
          var _iteratorError10 = undefined;

          try {
            for (var _iterator10 = consumerInfos[Symbol.iterator](), _step10; !(_iteratorNormalCompletion10 = (_step10 = _iterator10.next()).done); _iteratorNormalCompletion10 = true) {
              var info = _step10.value;
              if (info.kind !== kind) continue;
              remoteMediaObj.ssrcs.push({
                id: info.ssrc,
                attribute: 'msid',
                value: "".concat(info.streamId, " ").concat(info.trackId)
              });
              remoteMediaObj.ssrcs.push({
                id: info.ssrc,
                attribute: 'mslabel',
                value: info.streamId
              });
              remoteMediaObj.ssrcs.push({
                id: info.ssrc,
                attribute: 'label',
                value: info.trackId
              });
              remoteMediaObj.ssrcs.push({
                id: info.ssrc,
                attribute: 'cname',
                value: info.cname
              });

              if (info.rtxSsrc) {
                remoteMediaObj.ssrcs.push({
                  id: info.rtxSsrc,
                  attribute: 'msid',
                  value: "".concat(info.streamId, " ").concat(info.trackId)
                });
                remoteMediaObj.ssrcs.push({
                  id: info.rtxSsrc,
                  attribute: 'mslabel',
                  value: info.streamId
                });
                remoteMediaObj.ssrcs.push({
                  id: info.rtxSsrc,
                  attribute: 'label',
                  value: info.trackId
                });
                remoteMediaObj.ssrcs.push({
                  id: info.rtxSsrc,
                  attribute: 'cname',
                  value: info.cname
                }); // Associate original and retransmission SSRC.

                remoteMediaObj.ssrcGroups.push({
                  semantics: 'FID',
                  ssrcs: "".concat(info.ssrc, " ").concat(info.rtxSsrc)
                });
              }
            } // Push it.

          } catch (err) {
            _didIteratorError10 = true;
            _iteratorError10 = err;
          } finally {
            try {
              if (!_iteratorNormalCompletion10 && _iterator10.return != null) {
                _iterator10.return();
              }
            } finally {
              if (_didIteratorError10) {
                throw _iteratorError10;
              }
            }
          }

          sdpObj.media.push(remoteMediaObj);
        };

        for (var _iterator6 = kinds[Symbol.iterator](), _step6; !(_iteratorNormalCompletion6 = (_step6 = _iterator6.next()).done); _iteratorNormalCompletion6 = true) {
          _loop2();
        }
      } catch (err) {
        _didIteratorError6 = true;
        _iteratorError6 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion6 && _iterator6.return != null) {
            _iterator6.return();
          }
        } finally {
          if (_didIteratorError6) {
            throw _iteratorError6;
          }
        }
      }

      var sdp = _sdpTransform.default.write(sdpObj);

      return sdp;
    }
  }]);

  return RecvRemoteSdp;
}(RemoteSdp);

var RemotePlanBSdp = function RemotePlanBSdp(direction, rtpParametersByKind) {
  _classCallCheck(this, RemotePlanBSdp);

  logger.debug('constructor() [direction:%s, rtpParametersByKind:%o]', direction, rtpParametersByKind);

  switch (direction) {
    case 'send':
      return new SendRemoteSdp(rtpParametersByKind);

    case 'recv':
      return new RecvRemoteSdp(rtpParametersByKind);
  }
};

exports.default = RemotePlanBSdp;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemoteUnifiedPlanSdp.js":
/*!************************************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemoteUnifiedPlanSdp.js ***!
  \************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _sdpTransform = _interopRequireDefault(__webpack_require__(/*! sdp-transform */ "./node_modules/sdp-transform/lib/index.js"));

var _Logger = _interopRequireDefault(__webpack_require__(/*! ../../Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var utils = _interopRequireWildcard(__webpack_require__(/*! ../../utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

var logger = new _Logger.default('RemoteUnifiedPlanSdp');

var RemoteSdp =
/*#__PURE__*/
function () {
  function RemoteSdp(rtpParametersByKind) {
    _classCallCheck(this, RemoteSdp);

    // Generic sending RTP parameters for audio and video.
    // @type {Object}
    this._rtpParametersByKind = rtpParametersByKind; // Transport local parameters, including DTLS parameteres.
    // @type {Object}

    this._transportLocalParameters = null; // Transport remote parameters, including ICE parameters, ICE candidates
    // and DTLS parameteres.
    // @type {Object}

    this._transportRemoteParameters = null; // SDP global fields.
    // @type {Object}

    this._sdpGlobalFields = {
      id: utils.randomNumber(),
      version: 0
    };
  }

  _createClass(RemoteSdp, [{
    key: "setTransportLocalParameters",
    value: function setTransportLocalParameters(transportLocalParameters) {
      logger.debug('setTransportLocalParameters() [transportLocalParameters:%o]', transportLocalParameters);
      this._transportLocalParameters = transportLocalParameters;
    }
  }, {
    key: "setTransportRemoteParameters",
    value: function setTransportRemoteParameters(transportRemoteParameters) {
      logger.debug('setTransportRemoteParameters() [transportRemoteParameters:%o]', transportRemoteParameters);
      this._transportRemoteParameters = transportRemoteParameters;
    }
  }, {
    key: "updateTransportRemoteIceParameters",
    value: function updateTransportRemoteIceParameters(remoteIceParameters) {
      logger.debug('updateTransportRemoteIceParameters() [remoteIceParameters:%o]', remoteIceParameters);
      this._transportRemoteParameters.iceParameters = remoteIceParameters;
    }
  }]);

  return RemoteSdp;
}();

var SendRemoteSdp =
/*#__PURE__*/
function (_RemoteSdp) {
  _inherits(SendRemoteSdp, _RemoteSdp);

  function SendRemoteSdp(rtpParametersByKind) {
    _classCallCheck(this, SendRemoteSdp);

    return _possibleConstructorReturn(this, _getPrototypeOf(SendRemoteSdp).call(this, rtpParametersByKind));
  }

  _createClass(SendRemoteSdp, [{
    key: "createAnswerSdp",
    value: function createAnswerSdp(localSdpObj) {
      logger.debug('createAnswerSdp()');
      if (!this._transportLocalParameters) throw new Error('no transport local parameters');else if (!this._transportRemoteParameters) throw new Error('no transport remote parameters');
      var remoteIceParameters = this._transportRemoteParameters.iceParameters;
      var remoteIceCandidates = this._transportRemoteParameters.iceCandidates;
      var remoteDtlsParameters = this._transportRemoteParameters.dtlsParameters;
      var sdpObj = {};
      var bundleMids = (localSdpObj.media || []).filter(function (m) {
        return m.hasOwnProperty('mid');
      }).map(function (m) {
        return String(m.mid);
      }); // Increase our SDP version.

      this._sdpGlobalFields.version++;
      sdpObj.version = 0;
      sdpObj.origin = {
        address: '0.0.0.0',
        ipVer: 4,
        netType: 'IN',
        sessionId: this._sdpGlobalFields.id,
        sessionVersion: this._sdpGlobalFields.version,
        username: 'mediasoup-client'
      };
      sdpObj.name = '-';
      sdpObj.timing = {
        start: 0,
        stop: 0
      };
      sdpObj.icelite = remoteIceParameters.iceLite ? 'ice-lite' : null;
      sdpObj.msidSemantic = {
        semantic: 'WMS',
        token: '*'
      };

      if (bundleMids.length > 0) {
        sdpObj.groups = [{
          type: 'BUNDLE',
          mids: bundleMids.join(' ')
        }];
      }

      sdpObj.media = []; // NOTE: We take the latest fingerprint.

      var numFingerprints = remoteDtlsParameters.fingerprints.length;
      sdpObj.fingerprint = {
        type: remoteDtlsParameters.fingerprints[numFingerprints - 1].algorithm,
        hash: remoteDtlsParameters.fingerprints[numFingerprints - 1].value
      };
      var _iteratorNormalCompletion = true;
      var _didIteratorError = false;
      var _iteratorError = undefined;

      try {
        for (var _iterator = (localSdpObj.media || [])[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
          var localMediaObj = _step.value;
          var closed = localMediaObj.direction === 'inactive';
          var kind = localMediaObj.type;
          var codecs = this._rtpParametersByKind[kind].codecs;
          var headerExtensions = this._rtpParametersByKind[kind].headerExtensions;
          var remoteMediaObj = {};
          remoteMediaObj.type = localMediaObj.type;
          remoteMediaObj.port = 7;
          remoteMediaObj.protocol = 'RTP/SAVPF';
          remoteMediaObj.connection = {
            ip: '127.0.0.1',
            version: 4
          };
          remoteMediaObj.mid = localMediaObj.mid;
          remoteMediaObj.iceUfrag = remoteIceParameters.usernameFragment;
          remoteMediaObj.icePwd = remoteIceParameters.password;
          remoteMediaObj.candidates = [];
          var _iteratorNormalCompletion2 = true;
          var _didIteratorError2 = false;
          var _iteratorError2 = undefined;

          try {
            for (var _iterator2 = remoteIceCandidates[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
              var candidate = _step2.value;
              var candidateObj = {}; // mediasoup does not support non rtcp-mux so candidates component is
              // always RTP (1).

              candidateObj.component = 1;
              candidateObj.foundation = candidate.foundation;
              candidateObj.ip = candidate.ip;
              candidateObj.port = candidate.port;
              candidateObj.priority = candidate.priority;
              candidateObj.transport = candidate.protocol;
              candidateObj.type = candidate.type;
              if (candidate.tcpType) candidateObj.tcptype = candidate.tcpType;
              remoteMediaObj.candidates.push(candidateObj);
            }
          } catch (err) {
            _didIteratorError2 = true;
            _iteratorError2 = err;
          } finally {
            try {
              if (!_iteratorNormalCompletion2 && _iterator2.return != null) {
                _iterator2.return();
              }
            } finally {
              if (_didIteratorError2) {
                throw _iteratorError2;
              }
            }
          }

          remoteMediaObj.endOfCandidates = 'end-of-candidates'; // Announce support for ICE renomination.
          // https://tools.ietf.org/html/draft-thatcher-ice-renomination

          remoteMediaObj.iceOptions = 'renomination';

          switch (remoteDtlsParameters.role) {
            case 'client':
              remoteMediaObj.setup = 'active';
              break;

            case 'server':
              remoteMediaObj.setup = 'passive';
              break;
          }

          switch (localMediaObj.direction) {
            case 'sendrecv':
            case 'sendonly':
              remoteMediaObj.direction = 'recvonly';
              break;

            case 'recvonly':
            case 'inactive':
              remoteMediaObj.direction = 'inactive';
              break;
          }

          remoteMediaObj.rtp = [];
          remoteMediaObj.rtcpFb = [];
          remoteMediaObj.fmtp = [];
          var _iteratorNormalCompletion3 = true;
          var _didIteratorError3 = false;
          var _iteratorError3 = undefined;

          try {
            for (var _iterator3 = codecs[Symbol.iterator](), _step3; !(_iteratorNormalCompletion3 = (_step3 = _iterator3.next()).done); _iteratorNormalCompletion3 = true) {
              var codec = _step3.value;
              var rtp = {
                payload: codec.payloadType,
                codec: codec.name,
                rate: codec.clockRate
              };
              if (codec.channels > 1) rtp.encoding = codec.channels;
              remoteMediaObj.rtp.push(rtp);

              if (codec.parameters) {
                var paramFmtp = {
                  payload: codec.payloadType,
                  config: ''
                };

                var _arr = Object.keys(codec.parameters);

                for (var _i = 0; _i < _arr.length; _i++) {
                  var key = _arr[_i];
                  if (paramFmtp.config) paramFmtp.config += ';';
                  paramFmtp.config += "".concat(key, "=").concat(codec.parameters[key]);
                }

                if (paramFmtp.config) remoteMediaObj.fmtp.push(paramFmtp);
              }

              if (codec.rtcpFeedback) {
                var _iteratorNormalCompletion6 = true;
                var _didIteratorError6 = false;
                var _iteratorError6 = undefined;

                try {
                  for (var _iterator6 = codec.rtcpFeedback[Symbol.iterator](), _step6; !(_iteratorNormalCompletion6 = (_step6 = _iterator6.next()).done); _iteratorNormalCompletion6 = true) {
                    var fb = _step6.value;
                    remoteMediaObj.rtcpFb.push({
                      payload: codec.payloadType,
                      type: fb.type,
                      subtype: fb.parameter || ''
                    });
                  }
                } catch (err) {
                  _didIteratorError6 = true;
                  _iteratorError6 = err;
                } finally {
                  try {
                    if (!_iteratorNormalCompletion6 && _iterator6.return != null) {
                      _iterator6.return();
                    }
                  } finally {
                    if (_didIteratorError6) {
                      throw _iteratorError6;
                    }
                  }
                }
              }
            }
          } catch (err) {
            _didIteratorError3 = true;
            _iteratorError3 = err;
          } finally {
            try {
              if (!_iteratorNormalCompletion3 && _iterator3.return != null) {
                _iterator3.return();
              }
            } finally {
              if (_didIteratorError3) {
                throw _iteratorError3;
              }
            }
          }

          remoteMediaObj.payloads = codecs.map(function (codec) {
            return codec.payloadType;
          }).join(' '); // NOTE: Firefox does not like a=extmap lines if a=inactive.

          if (!closed) {
            remoteMediaObj.ext = [];
            var _iteratorNormalCompletion4 = true;
            var _didIteratorError4 = false;
            var _iteratorError4 = undefined;

            try {
              var _loop = function _loop() {
                var ext = _step4.value;
                // Don't add a header extension if not present in the offer.
                var matchedLocalExt = (localMediaObj.ext || []).find(function (localExt) {
                  return localExt.uri === ext.uri;
                });
                if (!matchedLocalExt) return "continue";
                remoteMediaObj.ext.push({
                  uri: ext.uri,
                  value: ext.id
                });
              };

              for (var _iterator4 = headerExtensions[Symbol.iterator](), _step4; !(_iteratorNormalCompletion4 = (_step4 = _iterator4.next()).done); _iteratorNormalCompletion4 = true) {
                var _ret = _loop();

                if (_ret === "continue") continue;
              }
            } catch (err) {
              _didIteratorError4 = true;
              _iteratorError4 = err;
            } finally {
              try {
                if (!_iteratorNormalCompletion4 && _iterator4.return != null) {
                  _iterator4.return();
                }
              } finally {
                if (_didIteratorError4) {
                  throw _iteratorError4;
                }
              }
            }
          } // Simulcast.


          if (localMediaObj.simulcast_03) {
            // eslint-disable-next-line camelcase
            remoteMediaObj.simulcast_03 = {
              value: localMediaObj.simulcast_03.value.replace(/send/g, 'recv')
            };
            remoteMediaObj.rids = [];
            var _iteratorNormalCompletion5 = true;
            var _didIteratorError5 = false;
            var _iteratorError5 = undefined;

            try {
              for (var _iterator5 = (localMediaObj.rids || [])[Symbol.iterator](), _step5; !(_iteratorNormalCompletion5 = (_step5 = _iterator5.next()).done); _iteratorNormalCompletion5 = true) {
                var rid = _step5.value;
                if (rid.direction !== 'send') continue;
                remoteMediaObj.rids.push({
                  id: rid.id,
                  direction: 'recv'
                });
              }
            } catch (err) {
              _didIteratorError5 = true;
              _iteratorError5 = err;
            } finally {
              try {
                if (!_iteratorNormalCompletion5 && _iterator5.return != null) {
                  _iterator5.return();
                }
              } finally {
                if (_didIteratorError5) {
                  throw _iteratorError5;
                }
              }
            }
          }

          remoteMediaObj.rtcpMux = 'rtcp-mux';
          remoteMediaObj.rtcpRsize = 'rtcp-rsize'; // Push it.

          sdpObj.media.push(remoteMediaObj);
        }
      } catch (err) {
        _didIteratorError = true;
        _iteratorError = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion && _iterator.return != null) {
            _iterator.return();
          }
        } finally {
          if (_didIteratorError) {
            throw _iteratorError;
          }
        }
      }

      var sdp = _sdpTransform.default.write(sdpObj);

      return sdp;
    }
  }]);

  return SendRemoteSdp;
}(RemoteSdp);

var RecvRemoteSdp =
/*#__PURE__*/
function (_RemoteSdp2) {
  _inherits(RecvRemoteSdp, _RemoteSdp2);

  function RecvRemoteSdp(rtpParametersByKind) {
    _classCallCheck(this, RecvRemoteSdp);

    return _possibleConstructorReturn(this, _getPrototypeOf(RecvRemoteSdp).call(this, rtpParametersByKind));
  }
  /**
   * @param {Array<Object>} consumerInfos - Consumer informations.
   * @return {String}
   */


  _createClass(RecvRemoteSdp, [{
    key: "createOfferSdp",
    value: function createOfferSdp(consumerInfos) {
      logger.debug('createOfferSdp()');
      if (!this._transportRemoteParameters) throw new Error('no transport remote parameters');
      var remoteIceParameters = this._transportRemoteParameters.iceParameters;
      var remoteIceCandidates = this._transportRemoteParameters.iceCandidates;
      var remoteDtlsParameters = this._transportRemoteParameters.dtlsParameters;
      var sdpObj = {};
      var mids = consumerInfos.map(function (info) {
        return String(info.mid);
      }); // Increase our SDP version.

      this._sdpGlobalFields.version++;
      sdpObj.version = 0;
      sdpObj.origin = {
        address: '0.0.0.0',
        ipVer: 4,
        netType: 'IN',
        sessionId: this._sdpGlobalFields.id,
        sessionVersion: this._sdpGlobalFields.version,
        username: 'mediasoup-client'
      };
      sdpObj.name = '-';
      sdpObj.timing = {
        start: 0,
        stop: 0
      };
      sdpObj.icelite = remoteIceParameters.iceLite ? 'ice-lite' : null;
      sdpObj.msidSemantic = {
        semantic: 'WMS',
        token: '*'
      };

      if (mids.length > 0) {
        sdpObj.groups = [{
          type: 'BUNDLE',
          mids: mids.join(' ')
        }];
      }

      sdpObj.media = []; // NOTE: We take the latest fingerprint.

      var numFingerprints = remoteDtlsParameters.fingerprints.length;
      sdpObj.fingerprint = {
        type: remoteDtlsParameters.fingerprints[numFingerprints - 1].algorithm,
        hash: remoteDtlsParameters.fingerprints[numFingerprints - 1].value
      };
      var _iteratorNormalCompletion7 = true;
      var _didIteratorError7 = false;
      var _iteratorError7 = undefined;

      try {
        for (var _iterator7 = consumerInfos[Symbol.iterator](), _step7; !(_iteratorNormalCompletion7 = (_step7 = _iterator7.next()).done); _iteratorNormalCompletion7 = true) {
          var info = _step7.value;
          var closed = info.closed;
          var kind = info.kind;
          var codecs = void 0;
          var headerExtensions = void 0;

          if (info.kind !== 'application') {
            codecs = this._rtpParametersByKind[kind].codecs;
            headerExtensions = this._rtpParametersByKind[kind].headerExtensions;
          }

          var remoteMediaObj = {};

          if (info.kind !== 'application') {
            remoteMediaObj.type = kind;
            remoteMediaObj.port = 7;
            remoteMediaObj.protocol = 'RTP/SAVPF';
            remoteMediaObj.connection = {
              ip: '127.0.0.1',
              version: 4
            };
            remoteMediaObj.mid = info.mid;
            remoteMediaObj.msid = "".concat(info.streamId, " ").concat(info.trackId);
          } else {
            remoteMediaObj.type = kind;
            remoteMediaObj.port = 9;
            remoteMediaObj.protocol = 'DTLS/SCTP';
            remoteMediaObj.connection = {
              ip: '127.0.0.1',
              version: 4
            };
            remoteMediaObj.mid = info.mid;
          }

          remoteMediaObj.iceUfrag = remoteIceParameters.usernameFragment;
          remoteMediaObj.icePwd = remoteIceParameters.password;
          remoteMediaObj.candidates = [];
          var _iteratorNormalCompletion8 = true;
          var _didIteratorError8 = false;
          var _iteratorError8 = undefined;

          try {
            for (var _iterator8 = remoteIceCandidates[Symbol.iterator](), _step8; !(_iteratorNormalCompletion8 = (_step8 = _iterator8.next()).done); _iteratorNormalCompletion8 = true) {
              var candidate = _step8.value;
              var candidateObj = {}; // mediasoup does not support non rtcp-mux so candidates component is
              // always RTP (1).

              candidateObj.component = 1;
              candidateObj.foundation = candidate.foundation;
              candidateObj.ip = candidate.ip;
              candidateObj.port = candidate.port;
              candidateObj.priority = candidate.priority;
              candidateObj.transport = candidate.protocol;
              candidateObj.type = candidate.type;
              if (candidate.tcpType) candidateObj.tcptype = candidate.tcpType;
              remoteMediaObj.candidates.push(candidateObj);
            }
          } catch (err) {
            _didIteratorError8 = true;
            _iteratorError8 = err;
          } finally {
            try {
              if (!_iteratorNormalCompletion8 && _iterator8.return != null) {
                _iterator8.return();
              }
            } finally {
              if (_didIteratorError8) {
                throw _iteratorError8;
              }
            }
          }

          remoteMediaObj.endOfCandidates = 'end-of-candidates'; // Announce support for ICE renomination.
          // https://tools.ietf.org/html/draft-thatcher-ice-renomination

          remoteMediaObj.iceOptions = 'renomination';
          remoteMediaObj.setup = 'actpass';

          if (info.kind !== 'application') {
            if (!closed) remoteMediaObj.direction = 'sendonly';else remoteMediaObj.direction = 'inactive';
            remoteMediaObj.rtp = [];
            remoteMediaObj.rtcpFb = [];
            remoteMediaObj.fmtp = [];
            var _iteratorNormalCompletion9 = true;
            var _didIteratorError9 = false;
            var _iteratorError9 = undefined;

            try {
              for (var _iterator9 = codecs[Symbol.iterator](), _step9; !(_iteratorNormalCompletion9 = (_step9 = _iterator9.next()).done); _iteratorNormalCompletion9 = true) {
                var codec = _step9.value;
                var rtp = {
                  payload: codec.payloadType,
                  codec: codec.name,
                  rate: codec.clockRate
                };
                if (codec.channels > 1) rtp.encoding = codec.channels;
                remoteMediaObj.rtp.push(rtp);

                if (codec.parameters) {
                  var paramFmtp = {
                    payload: codec.payloadType,
                    config: ''
                  };

                  var _arr2 = Object.keys(codec.parameters);

                  for (var _i2 = 0; _i2 < _arr2.length; _i2++) {
                    var key = _arr2[_i2];
                    if (paramFmtp.config) paramFmtp.config += ';';
                    paramFmtp.config += "".concat(key, "=").concat(codec.parameters[key]);
                  }

                  if (paramFmtp.config) remoteMediaObj.fmtp.push(paramFmtp);
                }

                if (codec.rtcpFeedback) {
                  var _iteratorNormalCompletion11 = true;
                  var _didIteratorError11 = false;
                  var _iteratorError11 = undefined;

                  try {
                    for (var _iterator11 = codec.rtcpFeedback[Symbol.iterator](), _step11; !(_iteratorNormalCompletion11 = (_step11 = _iterator11.next()).done); _iteratorNormalCompletion11 = true) {
                      var fb = _step11.value;
                      remoteMediaObj.rtcpFb.push({
                        payload: codec.payloadType,
                        type: fb.type,
                        subtype: fb.parameter || ''
                      });
                    }
                  } catch (err) {
                    _didIteratorError11 = true;
                    _iteratorError11 = err;
                  } finally {
                    try {
                      if (!_iteratorNormalCompletion11 && _iterator11.return != null) {
                        _iterator11.return();
                      }
                    } finally {
                      if (_didIteratorError11) {
                        throw _iteratorError11;
                      }
                    }
                  }
                }
              }
            } catch (err) {
              _didIteratorError9 = true;
              _iteratorError9 = err;
            } finally {
              try {
                if (!_iteratorNormalCompletion9 && _iterator9.return != null) {
                  _iterator9.return();
                }
              } finally {
                if (_didIteratorError9) {
                  throw _iteratorError9;
                }
              }
            }

            remoteMediaObj.payloads = codecs.map(function (codec) {
              return codec.payloadType;
            }).join(' '); // NOTE: Firefox does not like a=extmap lines if a=inactive.

            if (!closed) {
              remoteMediaObj.ext = [];
              var _iteratorNormalCompletion10 = true;
              var _didIteratorError10 = false;
              var _iteratorError10 = undefined;

              try {
                for (var _iterator10 = headerExtensions[Symbol.iterator](), _step10; !(_iteratorNormalCompletion10 = (_step10 = _iterator10.next()).done); _iteratorNormalCompletion10 = true) {
                  var ext = _step10.value;
                  // Ignore MID RTP extension for receiving media.
                  if (ext.uri === 'urn:ietf:params:rtp-hdrext:sdes:mid') continue;
                  remoteMediaObj.ext.push({
                    uri: ext.uri,
                    value: ext.id
                  });
                }
              } catch (err) {
                _didIteratorError10 = true;
                _iteratorError10 = err;
              } finally {
                try {
                  if (!_iteratorNormalCompletion10 && _iterator10.return != null) {
                    _iterator10.return();
                  }
                } finally {
                  if (_didIteratorError10) {
                    throw _iteratorError10;
                  }
                }
              }
            }

            remoteMediaObj.rtcpMux = 'rtcp-mux';
            remoteMediaObj.rtcpRsize = 'rtcp-rsize';

            if (!closed) {
              remoteMediaObj.ssrcs = [];
              remoteMediaObj.ssrcGroups = [];
              remoteMediaObj.ssrcs.push({
                id: info.ssrc,
                attribute: 'cname',
                value: info.cname
              });

              if (info.rtxSsrc) {
                remoteMediaObj.ssrcs.push({
                  id: info.rtxSsrc,
                  attribute: 'cname',
                  value: info.cname
                }); // Associate original and retransmission SSRC.

                remoteMediaObj.ssrcGroups.push({
                  semantics: 'FID',
                  ssrcs: "".concat(info.ssrc, " ").concat(info.rtxSsrc)
                });
              }
            }
          } else {
            remoteMediaObj.payloads = 5000;
            remoteMediaObj.sctpmap = {
              app: 'webrtc-datachannel',
              maxMessageSize: 256,
              sctpmapNumber: 5000
            };
          } // Push it.


          sdpObj.media.push(remoteMediaObj);
        }
      } catch (err) {
        _didIteratorError7 = true;
        _iteratorError7 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion7 && _iterator7.return != null) {
            _iterator7.return();
          }
        } finally {
          if (_didIteratorError7) {
            throw _iteratorError7;
          }
        }
      }

      var sdp = _sdpTransform.default.write(sdpObj);

      return sdp;
    }
  }]);

  return RecvRemoteSdp;
}(RemoteSdp);

var RemoteUnifiedPlanSdp = function RemoteUnifiedPlanSdp(direction, rtpParametersByKind) {
  _classCallCheck(this, RemoteUnifiedPlanSdp);

  logger.debug('constructor() [direction:%s, rtpParametersByKind:%o]', direction, rtpParametersByKind);

  switch (direction) {
    case 'send':
      return new SendRemoteSdp(rtpParametersByKind);

    case 'recv':
      return new RecvRemoteSdp(rtpParametersByKind);
  }
};

exports.default = RemoteUnifiedPlanSdp;

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/commonUtils.js":
/*!***************************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/sdp/commonUtils.js ***!
  \***************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.extractRtpCapabilities = extractRtpCapabilities;
exports.extractDtlsParameters = extractDtlsParameters;

var _sdpTransform = _interopRequireDefault(__webpack_require__(/*! sdp-transform */ "./node_modules/sdp-transform/lib/index.js"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

/**
 * Extract RTP capabilities from a SDP.
 *
 * @param {Object} sdpObj - SDP Object generated by sdp-transform.
 * @return {RTCRtpCapabilities}
 */
function extractRtpCapabilities(sdpObj) {
  // Map of RtpCodecParameters indexed by payload type.
  var codecsMap = new Map(); // Array of RtpHeaderExtensions.

  var headerExtensions = []; // Whether a m=audio/video section has been already found.

  var gotAudio = false;
  var gotVideo = false;
  var _iteratorNormalCompletion = true;
  var _didIteratorError = false;
  var _iteratorError = undefined;

  try {
    for (var _iterator = sdpObj.media[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
      var m = _step.value;
      var kind = m.type;

      switch (kind) {
        case 'audio':
          {
            if (gotAudio) continue;
            gotAudio = true;
            break;
          }

        case 'video':
          {
            if (gotVideo) continue;
            gotVideo = true;
            break;
          }

        default:
          {
            continue;
          }
      } // Get codecs.


      var _iteratorNormalCompletion2 = true;
      var _didIteratorError2 = false;
      var _iteratorError2 = undefined;

      try {
        for (var _iterator2 = m.rtp[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
          var rtp = _step2.value;
          var codec = {
            name: rtp.codec,
            mimeType: "".concat(kind, "/").concat(rtp.codec),
            kind: kind,
            clockRate: rtp.rate,
            preferredPayloadType: rtp.payload,
            channels: rtp.encoding,
            rtcpFeedback: [],
            parameters: {}
          };
          if (codec.kind !== 'audio') delete codec.channels;else if (!codec.channels) codec.channels = 1;
          codecsMap.set(codec.preferredPayloadType, codec);
        } // Get codec parameters.

      } catch (err) {
        _didIteratorError2 = true;
        _iteratorError2 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion2 && _iterator2.return != null) {
            _iterator2.return();
          }
        } finally {
          if (_didIteratorError2) {
            throw _iteratorError2;
          }
        }
      }

      var _iteratorNormalCompletion3 = true;
      var _didIteratorError3 = false;
      var _iteratorError3 = undefined;

      try {
        for (var _iterator3 = (m.fmtp || [])[Symbol.iterator](), _step3; !(_iteratorNormalCompletion3 = (_step3 = _iterator3.next()).done); _iteratorNormalCompletion3 = true) {
          var fmtp = _step3.value;

          var parameters = _sdpTransform.default.parseFmtpConfig(fmtp.config);

          var _codec = codecsMap.get(fmtp.payload);

          if (!_codec) continue;
          _codec.parameters = parameters;
        } // Get RTCP feedback for each codec.

      } catch (err) {
        _didIteratorError3 = true;
        _iteratorError3 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion3 && _iterator3.return != null) {
            _iterator3.return();
          }
        } finally {
          if (_didIteratorError3) {
            throw _iteratorError3;
          }
        }
      }

      var _iteratorNormalCompletion4 = true;
      var _didIteratorError4 = false;
      var _iteratorError4 = undefined;

      try {
        for (var _iterator4 = (m.rtcpFb || [])[Symbol.iterator](), _step4; !(_iteratorNormalCompletion4 = (_step4 = _iterator4.next()).done); _iteratorNormalCompletion4 = true) {
          var fb = _step4.value;

          var _codec2 = codecsMap.get(fb.payload);

          if (!_codec2) continue;
          var feedback = {
            type: fb.type,
            parameter: fb.subtype
          };
          if (!feedback.parameter) delete feedback.parameter;

          _codec2.rtcpFeedback.push(feedback);
        } // Get RTP header extensions.

      } catch (err) {
        _didIteratorError4 = true;
        _iteratorError4 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion4 && _iterator4.return != null) {
            _iterator4.return();
          }
        } finally {
          if (_didIteratorError4) {
            throw _iteratorError4;
          }
        }
      }

      var _iteratorNormalCompletion5 = true;
      var _didIteratorError5 = false;
      var _iteratorError5 = undefined;

      try {
        for (var _iterator5 = (m.ext || [])[Symbol.iterator](), _step5; !(_iteratorNormalCompletion5 = (_step5 = _iterator5.next()).done); _iteratorNormalCompletion5 = true) {
          var ext = _step5.value;
          var headerExtension = {
            kind: kind,
            uri: ext.uri,
            preferredId: ext.value
          };
          headerExtensions.push(headerExtension);
        }
      } catch (err) {
        _didIteratorError5 = true;
        _iteratorError5 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion5 && _iterator5.return != null) {
            _iterator5.return();
          }
        } finally {
          if (_didIteratorError5) {
            throw _iteratorError5;
          }
        }
      }
    }
  } catch (err) {
    _didIteratorError = true;
    _iteratorError = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion && _iterator.return != null) {
        _iterator.return();
      }
    } finally {
      if (_didIteratorError) {
        throw _iteratorError;
      }
    }
  }

  var rtpCapabilities = {
    codecs: Array.from(codecsMap.values()),
    headerExtensions: headerExtensions,
    fecMechanisms: [] // TODO

  };
  return rtpCapabilities;
}
/**
 * Extract DTLS parameters from a SDP.
 *
 * @param {Object} sdpObj - SDP Object generated by sdp-transform.
 * @return {RTCDtlsParameters}
 */


function extractDtlsParameters(sdpObj) {
  var media = getFirstActiveMediaSection(sdpObj);
  var fingerprint = media.fingerprint || sdpObj.fingerprint;
  var role;

  switch (media.setup) {
    case 'active':
      role = 'client';
      break;

    case 'passive':
      role = 'server';
      break;

    case 'actpass':
      role = 'auto';
      break;
  }

  var dtlsParameters = {
    role: role,
    fingerprints: [{
      algorithm: fingerprint.type,
      value: fingerprint.hash
    }]
  };
  return dtlsParameters;
}
/**
 * Get the first acive media section.
 *
 * @private
 * @param {Object} sdpObj - SDP Object generated by sdp-transform.
 * @return {Object} SDP media section as parsed by sdp-transform.
 */


function getFirstActiveMediaSection(sdpObj) {
  return (sdpObj.media || []).find(function (m) {
    return m.iceUfrag && m.port !== 0;
  });
}

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/plainRtpUtils.js":
/*!*****************************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/sdp/plainRtpUtils.js ***!
  \*****************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.extractPlainRtpParametersByKind = extractPlainRtpParametersByKind;
exports.fillRtpParametersForKind = fillRtpParametersForKind;

/**
 * Extract plain RTP parameters from a SDP.
 *
 * @param {Object} sdpObj - SDP Object generated by sdp-transform.
 * @param {String} kind - media type.
 * @return {PlainRtpParameters}
 */
function extractPlainRtpParametersByKind(sdpObj, kind) {
  var mSection = (sdpObj.media || []).find(function (m) {
    return m.type === kind;
  });
  if (!mSection) throw new Error("m=".concat(kind, " section not found"));
  var plainRtpParameters = {
    ip: sdpObj.connection.ip,
    port: mSection.port
  };
  return plainRtpParameters;
}
/**
 * Fill the given RTP parameters for the given media type.
 *
 * @param {RTCRtpParameters} rtpParameters -  RTP parameters to be filled.
 * @param {Object} sdpObj - Local SDP Object generated by sdp-transform.
 * @param {String} kind - media type.
 */


function fillRtpParametersForKind(rtpParameters, sdpObj, kind) {
  var rtcp = {
    cname: null,
    reducedSize: true,
    mux: true
  };
  var mSection = (sdpObj.media || []).find(function (m) {
    return m.type === kind;
  });
  if (!mSection) throw new Error("m=".concat(kind, " section not found")); // Get the SSRC and CNAME.

  var ssrcCnameLine = (mSection.ssrcs || []).find(function (line) {
    return line.attribute === 'cname';
  });
  var ssrc;

  if (ssrcCnameLine) {
    ssrc = ssrcCnameLine.id;
    rtcp.cname = ssrcCnameLine.value;
  } // Fill RTP parameters.


  rtpParameters.rtcp = rtcp;
  rtpParameters.encodings = [];
  var encoding = {
    ssrc: ssrc
  };
  rtpParameters.encodings.push(encoding);
}

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/planBUtils.js":
/*!**************************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/sdp/planBUtils.js ***!
  \**************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.fillRtpParametersForTrack = fillRtpParametersForTrack;
exports.addSimulcastForTrack = addSimulcastForTrack;

function _slicedToArray(arr, i) { return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _nonIterableRest(); }

function _nonIterableRest() { throw new TypeError("Invalid attempt to destructure non-iterable instance"); }

function _iterableToArrayLimit(arr, i) { var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"] != null) _i["return"](); } finally { if (_d) throw _e; } } return _arr; }

function _arrayWithHoles(arr) { if (Array.isArray(arr)) return arr; }

/**
 * Fill the given RTP parameters for the given track.
 *
 * @param {RTCRtpParameters} rtpParameters -  RTP parameters to be filled.
 * @param {Object} sdpObj - Local SDP Object generated by sdp-transform.
 * @param {MediaStreamTrack} track
 */
function fillRtpParametersForTrack(rtpParameters, sdpObj, track) {
  var kind = track.kind;
  var rtcp = {
    cname: null,
    reducedSize: true,
    mux: true
  };
  var mSection = (sdpObj.media || []).find(function (m) {
    return m.type === kind;
  });
  if (!mSection) throw new Error("m=".concat(kind, " section not found")); // First media SSRC (or the only one).

  var firstSsrc; // Get all the SSRCs.

  var ssrcs = new Set();
  var _iteratorNormalCompletion = true;
  var _didIteratorError = false;
  var _iteratorError = undefined;

  try {
    for (var _iterator = (mSection.ssrcs || [])[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
      var line = _step.value;
      if (line.attribute !== 'msid') continue;
      var trackId = line.value.split(' ')[1];

      if (trackId === track.id) {
        var ssrc = line.id;
        ssrcs.add(ssrc);
        if (!firstSsrc) firstSsrc = ssrc;
      }
    }
  } catch (err) {
    _didIteratorError = true;
    _iteratorError = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion && _iterator.return != null) {
        _iterator.return();
      }
    } finally {
      if (_didIteratorError) {
        throw _iteratorError;
      }
    }
  }

  if (ssrcs.size === 0) throw new Error("a=ssrc line not found for local track [track.id:".concat(track.id, "]")); // Get media and RTX SSRCs.

  var ssrcToRtxSsrc = new Map(); // First assume RTX is used.

  var _iteratorNormalCompletion2 = true;
  var _didIteratorError2 = false;
  var _iteratorError2 = undefined;

  try {
    for (var _iterator2 = (mSection.ssrcGroups || [])[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
      var _line = _step2.value;
      if (_line.semantics !== 'FID') continue;

      var _line$ssrcs$split = _line.ssrcs.split(/\s+/),
          _line$ssrcs$split2 = _slicedToArray(_line$ssrcs$split, 2),
          _ssrc = _line$ssrcs$split2[0],
          rtxSsrc = _line$ssrcs$split2[1];

      _ssrc = Number(_ssrc);
      rtxSsrc = Number(rtxSsrc);

      if (ssrcs.has(_ssrc)) {
        // Remove both the SSRC and RTX SSRC from the Set so later we know that they
        // are already handled.
        ssrcs.delete(_ssrc);
        ssrcs.delete(rtxSsrc); // Add to the map.

        ssrcToRtxSsrc.set(_ssrc, rtxSsrc);
      }
    } // If the Set of SSRCs is not empty it means that RTX is not being used, so take
    // media SSRCs from there.

  } catch (err) {
    _didIteratorError2 = true;
    _iteratorError2 = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion2 && _iterator2.return != null) {
        _iterator2.return();
      }
    } finally {
      if (_didIteratorError2) {
        throw _iteratorError2;
      }
    }
  }

  var _iteratorNormalCompletion3 = true;
  var _didIteratorError3 = false;
  var _iteratorError3 = undefined;

  try {
    for (var _iterator3 = ssrcs[Symbol.iterator](), _step3; !(_iteratorNormalCompletion3 = (_step3 = _iterator3.next()).done); _iteratorNormalCompletion3 = true) {
      var _ssrc2 = _step3.value;
      // Add to the map.
      ssrcToRtxSsrc.set(_ssrc2, null);
    } // Get RTCP info.

  } catch (err) {
    _didIteratorError3 = true;
    _iteratorError3 = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion3 && _iterator3.return != null) {
        _iterator3.return();
      }
    } finally {
      if (_didIteratorError3) {
        throw _iteratorError3;
      }
    }
  }

  var ssrcCnameLine = mSection.ssrcs.find(function (line) {
    return line.attribute === 'cname' && line.id === firstSsrc;
  });
  if (ssrcCnameLine) rtcp.cname = ssrcCnameLine.value; // Fill RTP parameters.

  rtpParameters.rtcp = rtcp;
  rtpParameters.encodings = [];
  var simulcast = ssrcToRtxSsrc.size > 1;
  var simulcastProfiles = ['low', 'medium', 'high'];
  var _iteratorNormalCompletion4 = true;
  var _didIteratorError4 = false;
  var _iteratorError4 = undefined;

  try {
    for (var _iterator4 = ssrcToRtxSsrc[Symbol.iterator](), _step4; !(_iteratorNormalCompletion4 = (_step4 = _iterator4.next()).done); _iteratorNormalCompletion4 = true) {
      var _step4$value = _slicedToArray(_step4.value, 2),
          _ssrc3 = _step4$value[0],
          rtxSsrc = _step4$value[1];

      var encoding = {
        ssrc: _ssrc3
      };
      if (rtxSsrc) encoding.rtx = {
        ssrc: rtxSsrc
      };
      if (simulcast) encoding.profile = simulcastProfiles.shift();
      rtpParameters.encodings.push(encoding);
    }
  } catch (err) {
    _didIteratorError4 = true;
    _iteratorError4 = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion4 && _iterator4.return != null) {
        _iterator4.return();
      }
    } finally {
      if (_didIteratorError4) {
        throw _iteratorError4;
      }
    }
  }
}
/**
 * Adds simulcast into the given SDP for the given track.
 *
 * @param {Object} sdpObj - Local SDP Object generated by sdp-transform.
 * @param {MediaStreamTrack} track
 */


function addSimulcastForTrack(sdpObj, track) {
  var kind = track.kind;
  var mSection = (sdpObj.media || []).find(function (m) {
    return m.type === kind;
  });
  if (!mSection) throw new Error("m=".concat(kind, " section not found"));
  var ssrc;
  var rtxSsrc;
  var msid; // Get the SSRC.

  var ssrcMsidLine = (mSection.ssrcs || []).find(function (line) {
    if (line.attribute !== 'msid') return false;
    var trackId = line.value.split(' ')[1];

    if (trackId === track.id) {
      ssrc = line.id;
      msid = line.value.split(' ')[0];
      return true;
    }
  });
  if (!ssrcMsidLine) throw new Error("a=ssrc line not found for local track [track.id:".concat(track.id, "]")); // Get the SSRC for RTX.

  (mSection.ssrcGroups || []).some(function (line) {
    if (line.semantics !== 'FID') return;
    var ssrcs = line.ssrcs.split(/\s+/);

    if (Number(ssrcs[0]) === ssrc) {
      rtxSsrc = Number(ssrcs[1]);
      return true;
    }
  });
  var ssrcCnameLine = mSection.ssrcs.find(function (line) {
    return line.attribute === 'cname' && line.id === ssrc;
  });
  if (!ssrcCnameLine) throw new Error("CNAME line not found for local track [track.id:".concat(track.id, "]"));
  var cname = ssrcCnameLine.value;
  var ssrc2 = ssrc + 1;
  var ssrc3 = ssrc + 2;
  mSection.ssrcGroups = mSection.ssrcGroups || [];
  mSection.ssrcGroups.push({
    semantics: 'SIM',
    ssrcs: "".concat(ssrc, " ").concat(ssrc2, " ").concat(ssrc3)
  });
  mSection.ssrcs.push({
    id: ssrc2,
    attribute: 'cname',
    value: cname
  });
  mSection.ssrcs.push({
    id: ssrc2,
    attribute: 'msid',
    value: "".concat(msid, " ").concat(track.id)
  });
  mSection.ssrcs.push({
    id: ssrc3,
    attribute: 'cname',
    value: cname
  });
  mSection.ssrcs.push({
    id: ssrc3,
    attribute: 'msid',
    value: "".concat(msid, " ").concat(track.id)
  });

  if (rtxSsrc) {
    var rtxSsrc2 = rtxSsrc + 1;
    var rtxSsrc3 = rtxSsrc + 2;
    mSection.ssrcGroups.push({
      semantics: 'FID',
      ssrcs: "".concat(ssrc2, " ").concat(rtxSsrc2)
    });
    mSection.ssrcs.push({
      id: rtxSsrc2,
      attribute: 'cname',
      value: cname
    });
    mSection.ssrcs.push({
      id: rtxSsrc2,
      attribute: 'msid',
      value: "".concat(msid, " ").concat(track.id)
    });
    mSection.ssrcGroups.push({
      semantics: 'FID',
      ssrcs: "".concat(ssrc3, " ").concat(rtxSsrc3)
    });
    mSection.ssrcs.push({
      id: rtxSsrc3,
      attribute: 'cname',
      value: cname
    });
    mSection.ssrcs.push({
      id: rtxSsrc3,
      attribute: 'msid',
      value: "".concat(msid, " ").concat(track.id)
    });
  }
}

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/unifiedPlanUtils.js":
/*!********************************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/handlers/sdp/unifiedPlanUtils.js ***!
  \********************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.fillRtpParametersForTrack = fillRtpParametersForTrack;
exports.addPlanBSimulcast = addPlanBSimulcast;

function _slicedToArray(arr, i) { return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _nonIterableRest(); }

function _nonIterableRest() { throw new TypeError("Invalid attempt to destructure non-iterable instance"); }

function _iterableToArrayLimit(arr, i) { var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"] != null) _i["return"](); } finally { if (_d) throw _e; } } return _arr; }

function _arrayWithHoles(arr) { if (Array.isArray(arr)) return arr; }

/**
 * Fill the given RTP parameters for the given mid or sending track.
 *
 * @param {RTCRtpParameters} rtpParameters -  RTP parameters to be filled.
 * @param {Object} sdpObj - Local SDP Object generated by sdp-transform.
 * @param {MediaStreamTrack} track
 * @param {String} [mid]
 * @param {Boolean} [planBSimulcast]
 */
function fillRtpParametersForTrack(rtpParameters, sdpObj, track) {
  var _ref = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {},
      _ref$mid = _ref.mid,
      mid = _ref$mid === void 0 ? null : _ref$mid,
      _ref$planBSimulcast = _ref.planBSimulcast,
      planBSimulcast = _ref$planBSimulcast === void 0 ? false : _ref$planBSimulcast;

  var mSection = findMediaSection(sdpObj, track, mid);
  if (mid !== null && mid !== undefined) rtpParameters.muxId = String(mid);
  rtpParameters.rtcp = {
    cname: null,
    reducedSize: true,
    mux: true
  }; // Get the SSRC and CNAME.

  var ssrcCnameLine = (mSection.ssrcs || []).find(function (line) {
    return line.attribute === 'cname';
  });
  if (!ssrcCnameLine) throw new Error('CNAME value not found');
  rtpParameters.rtcp.cname = ssrcCnameLine.value; // Standard simylcast based on a=simulcast and RID.

  if (!planBSimulcast) {
    // Get first (and may be the only one) ssrc.
    var ssrc = ssrcCnameLine.id; // Get a=rid lines.
    // Array of Objects with rid and profile keys.

    var simulcastStreams = [];
    var _iteratorNormalCompletion = true;
    var _didIteratorError = false;
    var _iteratorError = undefined;

    try {
      for (var _iterator = (mSection.rids || [])[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
        var rid = _step.value;
        if (rid.direction !== 'send') continue;
        if (/^low/.test(rid.id)) simulcastStreams.push({
          rid: rid.id,
          profile: 'low'
        });else if (/^medium/.test(rid.id)) simulcastStreams.push({
          rid: rid.id,
          profile: 'medium'
        });
        if (/^high/.test(rid.id)) simulcastStreams.push({
          rid: rid.id,
          profile: 'high'
        });
      } // Fill RTP parameters.

    } catch (err) {
      _didIteratorError = true;
      _iteratorError = err;
    } finally {
      try {
        if (!_iteratorNormalCompletion && _iterator.return != null) {
          _iterator.return();
        }
      } finally {
        if (_didIteratorError) {
          throw _iteratorError;
        }
      }
    }

    rtpParameters.encodings = [];

    if (simulcastStreams.length === 0) {
      var encoding = {
        ssrc: ssrc
      };
      rtpParameters.encodings.push(encoding);
    } else {
      for (var _i = 0; _i < simulcastStreams.length; _i++) {
        var simulcastStream = simulcastStreams[_i];
        var _encoding = {
          encodingId: simulcastStream.rid,
          profile: simulcastStream.profile
        };
        rtpParameters.encodings.push(_encoding);
      }
    }
  } // Simulcast based on PlanB.
  else {
      // First media SSRC (or the only one).
      var firstSsrc; // Get all the SSRCs.

      var ssrcs = new Set();
      var _iteratorNormalCompletion2 = true;
      var _didIteratorError2 = false;
      var _iteratorError2 = undefined;

      try {
        for (var _iterator2 = (mSection.ssrcs || [])[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
          var line = _step2.value;
          if (line.attribute !== 'msid') continue;
          var _ssrc = line.id;
          ssrcs.add(_ssrc);
          if (!firstSsrc) firstSsrc = _ssrc;
        }
      } catch (err) {
        _didIteratorError2 = true;
        _iteratorError2 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion2 && _iterator2.return != null) {
            _iterator2.return();
          }
        } finally {
          if (_didIteratorError2) {
            throw _iteratorError2;
          }
        }
      }

      if (ssrcs.size === 0) throw new Error('no a=ssrc lines found'); // Get media and RTX SSRCs.

      var ssrcToRtxSsrc = new Map(); // First assume RTX is used.

      var _iteratorNormalCompletion3 = true;
      var _didIteratorError3 = false;
      var _iteratorError3 = undefined;

      try {
        for (var _iterator3 = (mSection.ssrcGroups || [])[Symbol.iterator](), _step3; !(_iteratorNormalCompletion3 = (_step3 = _iterator3.next()).done); _iteratorNormalCompletion3 = true) {
          var _line = _step3.value;
          if (_line.semantics !== 'FID') continue;

          var _line$ssrcs$split = _line.ssrcs.split(/\s+/),
              _line$ssrcs$split2 = _slicedToArray(_line$ssrcs$split, 2),
              _ssrc2 = _line$ssrcs$split2[0],
              rtxSsrc = _line$ssrcs$split2[1];

          _ssrc2 = Number(_ssrc2);
          rtxSsrc = Number(rtxSsrc);

          if (ssrcs.has(_ssrc2)) {
            // Remove both the SSRC and RTX SSRC from the Set so later we know that they
            // are already handled.
            ssrcs.delete(_ssrc2);
            ssrcs.delete(rtxSsrc); // Add to the map.

            ssrcToRtxSsrc.set(_ssrc2, rtxSsrc);
          }
        } // If the Set of SSRCs is not empty it means that RTX is not being used, so take
        // media SSRCs from there.

      } catch (err) {
        _didIteratorError3 = true;
        _iteratorError3 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion3 && _iterator3.return != null) {
            _iterator3.return();
          }
        } finally {
          if (_didIteratorError3) {
            throw _iteratorError3;
          }
        }
      }

      var _iteratorNormalCompletion4 = true;
      var _didIteratorError4 = false;
      var _iteratorError4 = undefined;

      try {
        for (var _iterator4 = ssrcs[Symbol.iterator](), _step4; !(_iteratorNormalCompletion4 = (_step4 = _iterator4.next()).done); _iteratorNormalCompletion4 = true) {
          var _ssrc3 = _step4.value;
          // Add to the map.
          ssrcToRtxSsrc.set(_ssrc3, null);
        } // Fill RTP parameters.

      } catch (err) {
        _didIteratorError4 = true;
        _iteratorError4 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion4 && _iterator4.return != null) {
            _iterator4.return();
          }
        } finally {
          if (_didIteratorError4) {
            throw _iteratorError4;
          }
        }
      }

      rtpParameters.encodings = [];
      var simulcast = ssrcToRtxSsrc.size > 1;
      var simulcastProfiles = ['low', 'medium', 'high'];
      var _iteratorNormalCompletion5 = true;
      var _didIteratorError5 = false;
      var _iteratorError5 = undefined;

      try {
        for (var _iterator5 = ssrcToRtxSsrc[Symbol.iterator](), _step5; !(_iteratorNormalCompletion5 = (_step5 = _iterator5.next()).done); _iteratorNormalCompletion5 = true) {
          var _step5$value = _slicedToArray(_step5.value, 2),
              _ssrc4 = _step5$value[0],
              rtxSsrc = _step5$value[1];

          var _encoding2 = {
            ssrc: _ssrc4
          };
          if (rtxSsrc) _encoding2.rtx = {
            ssrc: rtxSsrc
          };
          if (simulcast) _encoding2.profile = simulcastProfiles.shift();
          rtpParameters.encodings.push(_encoding2);
        }
      } catch (err) {
        _didIteratorError5 = true;
        _iteratorError5 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion5 && _iterator5.return != null) {
            _iterator5.return();
          }
        } finally {
          if (_didIteratorError5) {
            throw _iteratorError5;
          }
        }
      }
    }
}
/**
 * Adds multi-ssrc based simulcast (PlanB) into the given SDP for the given mid
 * or track.
 * NOTE: This is for Chrome/Safari using Unified-Plan with legacy simulcast.
 *
 * @param {Object} sdpObj - Local SDP Object generated by sdp-transform.
 * @param {MediaStreamTrack} track
 * @param {String} [mid]
 */


function addPlanBSimulcast(sdpObj, track) {
  var _ref2 = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {},
      _ref2$mid = _ref2.mid,
      mid = _ref2$mid === void 0 ? null : _ref2$mid;

  var mSection = findMediaSection(sdpObj, track, mid); // Get the SSRC.

  var ssrcMsidLine = (mSection.ssrcs || []).find(function (line) {
    return line.attribute === 'msid';
  });
  if (!ssrcMsidLine) throw new Error('a=ssrc line with msid information not found');
  var ssrc = ssrcMsidLine.id;
  var msid = ssrcMsidLine.value.split(' ')[0];
  var rtxSsrc; // Get the SSRC for RTX.

  (mSection.ssrcGroups || []).some(function (line) {
    if (line.semantics !== 'FID') return;
    var ssrcs = line.ssrcs.split(/\s+/);

    if (Number(ssrcs[0]) === ssrc) {
      rtxSsrc = Number(ssrcs[1]);
      return true;
    }
  });
  var ssrcCnameLine = mSection.ssrcs.find(function (line) {
    return line.attribute === 'cname' && line.id === ssrc;
  });
  if (!ssrcCnameLine) throw new Error('CNAME line not found');
  var cname = ssrcCnameLine.value;
  var ssrc2 = ssrc + 1;
  var ssrc3 = ssrc + 2; // mSection.ssrcGroups = mSection.ssrcGroups || [];

  mSection.ssrcGroups = [];
  mSection.ssrcs = [];
  mSection.ssrcGroups.push({
    semantics: 'SIM',
    ssrcs: "".concat(ssrc, " ").concat(ssrc2, " ").concat(ssrc3)
  });
  mSection.ssrcs.push({
    id: ssrc,
    attribute: 'cname',
    value: cname
  });
  mSection.ssrcs.push({
    id: ssrc,
    attribute: 'msid',
    value: "".concat(msid, " ").concat(track.id)
  });
  mSection.ssrcs.push({
    id: ssrc2,
    attribute: 'cname',
    value: cname
  });
  mSection.ssrcs.push({
    id: ssrc2,
    attribute: 'msid',
    value: "".concat(msid, " ").concat(track.id)
  });
  mSection.ssrcs.push({
    id: ssrc3,
    attribute: 'cname',
    value: cname
  });
  mSection.ssrcs.push({
    id: ssrc3,
    attribute: 'msid',
    value: "".concat(msid, " ").concat(track.id)
  });

  if (rtxSsrc) {
    var rtxSsrc2 = rtxSsrc + 1;
    var rtxSsrc3 = rtxSsrc + 2;
    mSection.ssrcGroups.push({
      semantics: 'FID',
      ssrcs: "".concat(ssrc, " ").concat(rtxSsrc)
    });
    mSection.ssrcs.push({
      id: rtxSsrc,
      attribute: 'cname',
      value: cname
    });
    mSection.ssrcs.push({
      id: rtxSsrc,
      attribute: 'msid',
      value: "".concat(msid, " ").concat(track.id)
    });
    mSection.ssrcGroups.push({
      semantics: 'FID',
      ssrcs: "".concat(ssrc2, " ").concat(rtxSsrc2)
    });
    mSection.ssrcs.push({
      id: rtxSsrc2,
      attribute: 'cname',
      value: cname
    });
    mSection.ssrcs.push({
      id: rtxSsrc2,
      attribute: 'msid',
      value: "".concat(msid, " ").concat(track.id)
    });
    mSection.ssrcGroups.push({
      semantics: 'FID',
      ssrcs: "".concat(ssrc3, " ").concat(rtxSsrc3)
    });
    mSection.ssrcs.push({
      id: rtxSsrc3,
      attribute: 'cname',
      value: cname
    });
    mSection.ssrcs.push({
      id: rtxSsrc3,
      attribute: 'msid',
      value: "".concat(msid, " ").concat(track.id)
    });
  }
}

function findMediaSection(sdpObj, track, mid) {
  var mSection;

  if (mid !== null && mid !== undefined) {
    mid = String(mid);
    mSection = (sdpObj.media || []).find(function (m) {
      return String(m.mid) === mid;
    });
    if (!mSection) throw new Error("SDP section with mid=".concat(mid, " not found"));
  } else {
    mSection = (sdpObj.media || []).find(function (m) {
      return m.type === track.kind && m.msid && m.msid.split(' ')[1] === track.id;
    });
    if (!mSection) throw new Error("SDP section with a=msid containing track.id=".concat(track.id, " not found"));
  }

  return mSection;
}

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/index.js":
/*!********************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/index.js ***!
  \********************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.setDeviceHandler = setDeviceHandler;
exports.isDeviceSupported = isDeviceSupported;
exports.getDeviceInfo = getDeviceInfo;
exports.checkCapabilitiesForRoom = checkCapabilitiesForRoom;
Object.defineProperty(exports, "Room", {
  enumerable: true,
  get: function get() {
    return _Room.default;
  }
});
exports.internals = void 0;

var ortc = _interopRequireWildcard(__webpack_require__(/*! ./ortc */ "./node_modules/mediasoup-client/lib-es5/ortc.js"));

var _Device = _interopRequireDefault(__webpack_require__(/*! ./Device */ "./node_modules/mediasoup-client/lib-es5/Device.js"));

var _Room = _interopRequireDefault(__webpack_require__(/*! ./Room */ "./node_modules/mediasoup-client/lib-es5/Room.js"));

var internals = _interopRequireWildcard(__webpack_require__(/*! ./internals */ "./node_modules/mediasoup-client/lib-es5/internals.js"));

exports.internals = internals;

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

/**
 * Provides a custom RTC handler class and avoid auto-detection. Useful
 * for making mediasoup-client work with custom devices.
 *
 * NOTE: This function must be called upon library load.
 *
 * @param {Class} handler - A handler class.
 * @param {Object} [metadata] - Handler metadata.
 * @param {String} [metadata.flag] - Handler flag.
 * @param {String} [metadata.name] - Handler name.
 * @param {String} [metadata.version] - Handler version.
 * @param {Object} [metadata.bowser] - Handler bowser Object.
 */
function setDeviceHandler(handler, metadata) {
  _Device.default.setHandler(handler, metadata);
}
/**
 * Whether the current browser or device is supported.
 *
 * @return {Boolean}
 *
 * @example
 * isDeviceSupported()
 * // => true
 */


function isDeviceSupported() {
  return _Device.default.isSupported();
}
/**
 * Get information regarding the current browser or device.
 *
 * @return {Object} - Object with `name` (String) and version {String}.
 *
 * @example
 * getDeviceInfo()
 * // => { flag: 'chrome', name: 'Chrome', version: '59.0', bowser: {} }
 */


function getDeviceInfo() {
  return {
    flag: _Device.default.getFlag(),
    name: _Device.default.getName(),
    version: _Device.default.getVersion(),
    bowser: _Device.default.getBowser()
  };
}
/**
 * Check whether this device/browser can send/receive audio/video in a room
 * whose RTP capabilities are given.
 *
 * @param {Object} Room RTP capabilities.
 *
 * @return {Promise} Resolves to an Object with 'audio' and 'video' Booleans.
 */


function checkCapabilitiesForRoom(roomRtpCapabilities) {
  if (!_Device.default.isSupported()) return Promise.reject(new Error('current browser/device not supported'));
  return _Device.default.Handler.getNativeRtpCapabilities().then(function (nativeRtpCapabilities) {
    var extendedRtpCapabilities = ortc.getExtendedRtpCapabilities(nativeRtpCapabilities, roomRtpCapabilities);
    return {
      audio: ortc.canSend('audio', extendedRtpCapabilities),
      video: ortc.canSend('video', extendedRtpCapabilities)
    };
  });
}
/**
 * Expose the Room class.
 *
 * @example
 * const room = new Room();`
 */

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/internals.js":
/*!************************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/internals.js ***!
  \************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
Object.defineProperty(exports, "Logger", {
  enumerable: true,
  get: function get() {
    return _Logger.default;
  }
});
Object.defineProperty(exports, "EnhancedEventEmitter", {
  enumerable: true,
  get: function get() {
    return _EnhancedEventEmitter.default;
  }
});
Object.defineProperty(exports, "RemoteUnifiedPlanSdp", {
  enumerable: true,
  get: function get() {
    return _RemoteUnifiedPlanSdp.default;
  }
});
Object.defineProperty(exports, "RemotePlanBSdp", {
  enumerable: true,
  get: function get() {
    return _RemotePlanBSdp.default;
  }
});
Object.defineProperty(exports, "RemotePlainRtpSdp", {
  enumerable: true,
  get: function get() {
    return _RemotePlainRtpSdp.default;
  }
});
exports.sdpPlainRtpUtils = exports.sdpPlanBUtils = exports.sdpUnifiedPlanUtils = exports.sdpCommonUtils = exports.ortc = exports.utils = void 0;

var _Logger = _interopRequireDefault(__webpack_require__(/*! ./Logger */ "./node_modules/mediasoup-client/lib-es5/Logger.js"));

var _EnhancedEventEmitter = _interopRequireDefault(__webpack_require__(/*! ./EnhancedEventEmitter */ "./node_modules/mediasoup-client/lib-es5/EnhancedEventEmitter.js"));

var utils = _interopRequireWildcard(__webpack_require__(/*! ./utils */ "./node_modules/mediasoup-client/lib-es5/utils.js"));

exports.utils = utils;

var ortc = _interopRequireWildcard(__webpack_require__(/*! ./ortc */ "./node_modules/mediasoup-client/lib-es5/ortc.js"));

exports.ortc = ortc;

var sdpCommonUtils = _interopRequireWildcard(__webpack_require__(/*! ./handlers/sdp/commonUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/commonUtils.js"));

exports.sdpCommonUtils = sdpCommonUtils;

var sdpUnifiedPlanUtils = _interopRequireWildcard(__webpack_require__(/*! ./handlers/sdp/unifiedPlanUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/unifiedPlanUtils.js"));

exports.sdpUnifiedPlanUtils = sdpUnifiedPlanUtils;

var sdpPlanBUtils = _interopRequireWildcard(__webpack_require__(/*! ./handlers/sdp/planBUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/planBUtils.js"));

exports.sdpPlanBUtils = sdpPlanBUtils;

var sdpPlainRtpUtils = _interopRequireWildcard(__webpack_require__(/*! ./handlers/sdp/plainRtpUtils */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/plainRtpUtils.js"));

exports.sdpPlainRtpUtils = sdpPlainRtpUtils;

var _RemoteUnifiedPlanSdp = _interopRequireDefault(__webpack_require__(/*! ./handlers/sdp/RemoteUnifiedPlanSdp */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemoteUnifiedPlanSdp.js"));

var _RemotePlanBSdp = _interopRequireDefault(__webpack_require__(/*! ./handlers/sdp/RemotePlanBSdp */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemotePlanBSdp.js"));

var _RemotePlainRtpSdp = _interopRequireDefault(__webpack_require__(/*! ./handlers/sdp/RemotePlainRtpSdp */ "./node_modules/mediasoup-client/lib-es5/handlers/sdp/RemotePlainRtpSdp.js"));

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { var desc = Object.defineProperty && Object.getOwnPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : {}; if (desc.get || desc.set) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } } newObj.default = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/ortc.js":
/*!*******************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/ortc.js ***!
  \*******************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.getExtendedRtpCapabilities = getExtendedRtpCapabilities;
exports.getRtpCapabilities = getRtpCapabilities;
exports.getUnsupportedCodecs = getUnsupportedCodecs;
exports.canSend = canSend;
exports.canReceive = canReceive;
exports.getSendingRtpParameters = getSendingRtpParameters;
exports.getReceivingFullRtpParameters = getReceivingFullRtpParameters;

/**
 * Generate extended RTP capabilities for sending and receiving.
 *
 * @param {RTCRtpCapabilities} localCaps - Local capabilities.
 * @param {RTCRtpCapabilities} remoteCaps - Remote capabilities.
 *
 * @return {RTCExtendedRtpCapabilities}
 */
function getExtendedRtpCapabilities(localCaps, remoteCaps) {
  var extendedCaps = {
    codecs: [],
    headerExtensions: [],
    fecMechanisms: []
  }; // Match media codecs and keep the order preferred by remoteCaps.

  var _iteratorNormalCompletion = true;
  var _didIteratorError = false;
  var _iteratorError = undefined;

  try {
    var _loop = function _loop() {
      var remoteCodec = _step.value;
      // TODO: Ignore pseudo-codecs and feature codecs.
      if (remoteCodec.name === 'rtx') return "continue";
      var matchingLocalCodec = (localCaps.codecs || []).find(function (localCodec) {
        return matchCapCodecs(localCodec, remoteCodec);
      });

      if (matchingLocalCodec) {
        var extendedCodec = {
          name: remoteCodec.name,
          mimeType: remoteCodec.mimeType,
          kind: remoteCodec.kind,
          clockRate: remoteCodec.clockRate,
          sendPayloadType: matchingLocalCodec.preferredPayloadType,
          sendRtxPayloadType: null,
          recvPayloadType: remoteCodec.preferredPayloadType,
          recvRtxPayloadType: null,
          channels: remoteCodec.channels,
          rtcpFeedback: reduceRtcpFeedback(matchingLocalCodec, remoteCodec),
          parameters: remoteCodec.parameters
        };
        if (!extendedCodec.channels) delete extendedCodec.channels;
        extendedCaps.codecs.push(extendedCodec);
      }
    };

    for (var _iterator = (remoteCaps.codecs || [])[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
      var _ret = _loop();

      if (_ret === "continue") continue;
    } // Match RTX codecs.

  } catch (err) {
    _didIteratorError = true;
    _iteratorError = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion && _iterator.return != null) {
        _iterator.return();
      }
    } finally {
      if (_didIteratorError) {
        throw _iteratorError;
      }
    }
  }

  var _iteratorNormalCompletion2 = true;
  var _didIteratorError2 = false;
  var _iteratorError2 = undefined;

  try {
    var _loop2 = function _loop2() {
      var extendedCodec = _step2.value;
      var matchingLocalRtxCodec = (localCaps.codecs || []).find(function (localCodec) {
        return localCodec.name === 'rtx' && localCodec.parameters.apt === extendedCodec.sendPayloadType;
      });
      var matchingRemoteRtxCodec = (remoteCaps.codecs || []).find(function (remoteCodec) {
        return remoteCodec.name === 'rtx' && remoteCodec.parameters.apt === extendedCodec.recvPayloadType;
      });

      if (matchingLocalRtxCodec && matchingRemoteRtxCodec) {
        extendedCodec.sendRtxPayloadType = matchingLocalRtxCodec.preferredPayloadType;
        extendedCodec.recvRtxPayloadType = matchingRemoteRtxCodec.preferredPayloadType;
      }
    };

    for (var _iterator2 = (extendedCaps.codecs || [])[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
      _loop2();
    } // Match header extensions.

  } catch (err) {
    _didIteratorError2 = true;
    _iteratorError2 = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion2 && _iterator2.return != null) {
        _iterator2.return();
      }
    } finally {
      if (_didIteratorError2) {
        throw _iteratorError2;
      }
    }
  }

  var _iteratorNormalCompletion3 = true;
  var _didIteratorError3 = false;
  var _iteratorError3 = undefined;

  try {
    var _loop3 = function _loop3() {
      var remoteExt = _step3.value;
      var matchingLocalExt = (localCaps.headerExtensions || []).find(function (localExt) {
        return matchCapHeaderExtensions(localExt, remoteExt);
      });

      if (matchingLocalExt) {
        var extendedExt = {
          kind: remoteExt.kind,
          uri: remoteExt.uri,
          sendId: matchingLocalExt.preferredId,
          recvId: remoteExt.preferredId
        };
        extendedCaps.headerExtensions.push(extendedExt);
      }
    };

    for (var _iterator3 = (remoteCaps.headerExtensions || [])[Symbol.iterator](), _step3; !(_iteratorNormalCompletion3 = (_step3 = _iterator3.next()).done); _iteratorNormalCompletion3 = true) {
      _loop3();
    }
  } catch (err) {
    _didIteratorError3 = true;
    _iteratorError3 = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion3 && _iterator3.return != null) {
        _iterator3.return();
      }
    } finally {
      if (_didIteratorError3) {
        throw _iteratorError3;
      }
    }
  }

  return extendedCaps;
}
/**
 * Generate RTP capabilities for receiving media based on the given extended
 * RTP capabilities.
 *
 * @param {RTCExtendedRtpCapabilities} extendedRtpCapabilities
 *
 * @return {RTCRtpCapabilities}
 */


function getRtpCapabilities(extendedRtpCapabilities) {
  var caps = {
    codecs: [],
    headerExtensions: [],
    fecMechanisms: []
  };
  var _iteratorNormalCompletion4 = true;
  var _didIteratorError4 = false;
  var _iteratorError4 = undefined;

  try {
    for (var _iterator4 = extendedRtpCapabilities.codecs[Symbol.iterator](), _step4; !(_iteratorNormalCompletion4 = (_step4 = _iterator4.next()).done); _iteratorNormalCompletion4 = true) {
      var capCodec = _step4.value;
      var codec = {
        name: capCodec.name,
        mimeType: capCodec.mimeType,
        kind: capCodec.kind,
        clockRate: capCodec.clockRate,
        preferredPayloadType: capCodec.recvPayloadType,
        channels: capCodec.channels,
        rtcpFeedback: capCodec.rtcpFeedback,
        parameters: capCodec.parameters
      };
      if (!codec.channels) delete codec.channels;
      caps.codecs.push(codec); // Add RTX codec.

      if (capCodec.recvRtxPayloadType) {
        var rtxCapCodec = {
          name: 'rtx',
          mimeType: "".concat(capCodec.kind, "/rtx"),
          kind: capCodec.kind,
          clockRate: capCodec.clockRate,
          preferredPayloadType: capCodec.recvRtxPayloadType,
          parameters: {
            apt: capCodec.recvPayloadType
          }
        };
        caps.codecs.push(rtxCapCodec);
      } // TODO: In the future, we need to add FEC, CN, etc, codecs.

    }
  } catch (err) {
    _didIteratorError4 = true;
    _iteratorError4 = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion4 && _iterator4.return != null) {
        _iterator4.return();
      }
    } finally {
      if (_didIteratorError4) {
        throw _iteratorError4;
      }
    }
  }

  var _iteratorNormalCompletion5 = true;
  var _didIteratorError5 = false;
  var _iteratorError5 = undefined;

  try {
    for (var _iterator5 = extendedRtpCapabilities.headerExtensions[Symbol.iterator](), _step5; !(_iteratorNormalCompletion5 = (_step5 = _iterator5.next()).done); _iteratorNormalCompletion5 = true) {
      var capExt = _step5.value;
      var ext = {
        kind: capExt.kind,
        uri: capExt.uri,
        preferredId: capExt.recvId
      };
      caps.headerExtensions.push(ext);
    }
  } catch (err) {
    _didIteratorError5 = true;
    _iteratorError5 = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion5 && _iterator5.return != null) {
        _iterator5.return();
      }
    } finally {
      if (_didIteratorError5) {
        throw _iteratorError5;
      }
    }
  }

  caps.fecMechanisms = extendedRtpCapabilities.fecMechanisms;
  return caps;
}
/**
 * Get unsupported remote codecs.
 *
 * @param {RTCRtpCapabilities} remoteCaps - Remote capabilities.
 * @param {Array<Number>} mandatoryCodecPayloadTypes - List of codec PT values.
 * @param {RTCExtendedRtpCapabilities} extendedRtpCapabilities
 *
 * @return {Boolean}
 */


function getUnsupportedCodecs(remoteCaps, mandatoryCodecPayloadTypes, extendedRtpCapabilities) {
  // If not given just ignore.
  if (!Array.isArray(mandatoryCodecPayloadTypes)) return [];
  var unsupportedCodecs = [];
  var remoteCodecs = remoteCaps.codecs;
  var supportedCodecs = extendedRtpCapabilities.codecs;
  var _iteratorNormalCompletion6 = true;
  var _didIteratorError6 = false;
  var _iteratorError6 = undefined;

  try {
    var _loop4 = function _loop4() {
      var pt = _step6.value;

      if (!supportedCodecs.some(function (codec) {
        return codec.recvPayloadType === pt;
      })) {
        var unsupportedCodec = remoteCodecs.find(function (codec) {
          return codec.preferredPayloadType === pt;
        });
        if (!unsupportedCodec) throw new Error("mandatory codec PT ".concat(pt, " not found in remote codecs"));
        unsupportedCodecs.push(unsupportedCodec);
      }
    };

    for (var _iterator6 = mandatoryCodecPayloadTypes[Symbol.iterator](), _step6; !(_iteratorNormalCompletion6 = (_step6 = _iterator6.next()).done); _iteratorNormalCompletion6 = true) {
      _loop4();
    }
  } catch (err) {
    _didIteratorError6 = true;
    _iteratorError6 = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion6 && _iterator6.return != null) {
        _iterator6.return();
      }
    } finally {
      if (_didIteratorError6) {
        throw _iteratorError6;
      }
    }
  }

  return unsupportedCodecs;
}
/**
 * Whether media can be sent based on the given RTP capabilities.
 *
 * @param {String} kind
 * @param {RTCExtendedRtpCapabilities} extendedRtpCapabilities
 *
 * @return {Boolean}
 */


function canSend(kind, extendedRtpCapabilities) {
  return extendedRtpCapabilities.codecs.some(function (codec) {
    return codec.kind === kind;
  });
}
/**
 * Whether the given RTP parameters can be received with the given RTP
 * capabilities.
 *
 * @param {RTCRtpParameters} rtpParameters
 * @param {RTCExtendedRtpCapabilities} extendedRtpCapabilities
 *
 * @return {Boolean}
 */


function canReceive(rtpParameters, extendedRtpCapabilities) {
  if (rtpParameters.codecs.length === 0) return false;
  var firstMediaCodec = rtpParameters.codecs[0];
  return extendedRtpCapabilities.codecs.some(function (codec) {
    return codec.recvPayloadType === firstMediaCodec.payloadType;
  });
}
/**
 * Generate RTP parameters of the given kind for sending media.
 * Just the first media codec per kind is considered.
 * NOTE: muxId, encodings and rtcp fields are left empty.
 *
 * @param {kind} kind
 * @param {RTCExtendedRtpCapabilities} extendedRtpCapabilities
 *
 * @return {RTCRtpParameters}
 */


function getSendingRtpParameters(kind, extendedRtpCapabilities) {
  var params = {
    muxId: null,
    codecs: [],
    headerExtensions: [],
    encodings: [],
    rtcp: {}
  };
  var _iteratorNormalCompletion7 = true;
  var _didIteratorError7 = false;
  var _iteratorError7 = undefined;

  try {
    for (var _iterator7 = extendedRtpCapabilities.codecs[Symbol.iterator](), _step7; !(_iteratorNormalCompletion7 = (_step7 = _iterator7.next()).done); _iteratorNormalCompletion7 = true) {
      var capCodec = _step7.value;
      if (capCodec.kind !== kind) continue;
      var codec = {
        name: capCodec.name,
        mimeType: capCodec.mimeType,
        clockRate: capCodec.clockRate,
        payloadType: capCodec.sendPayloadType,
        channels: capCodec.channels,
        rtcpFeedback: capCodec.rtcpFeedback,
        parameters: capCodec.parameters
      };
      if (!codec.channels) delete codec.channels;
      params.codecs.push(codec); // Add RTX codec.

      if (capCodec.sendRtxPayloadType) {
        var rtxCodec = {
          name: 'rtx',
          mimeType: "".concat(capCodec.kind, "/rtx"),
          clockRate: capCodec.clockRate,
          payloadType: capCodec.sendRtxPayloadType,
          parameters: {
            apt: capCodec.sendPayloadType
          }
        };
        params.codecs.push(rtxCodec);
      } // NOTE: We assume a single media codec plus an optional RTX codec for now.
      // TODO: In the future, we need to add FEC, CN, etc, codecs.


      break;
    }
  } catch (err) {
    _didIteratorError7 = true;
    _iteratorError7 = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion7 && _iterator7.return != null) {
        _iterator7.return();
      }
    } finally {
      if (_didIteratorError7) {
        throw _iteratorError7;
      }
    }
  }

  var _iteratorNormalCompletion8 = true;
  var _didIteratorError8 = false;
  var _iteratorError8 = undefined;

  try {
    for (var _iterator8 = extendedRtpCapabilities.headerExtensions[Symbol.iterator](), _step8; !(_iteratorNormalCompletion8 = (_step8 = _iterator8.next()).done); _iteratorNormalCompletion8 = true) {
      var capExt = _step8.value;
      if (capExt.kind && capExt.kind !== kind) continue;
      var ext = {
        uri: capExt.uri,
        id: capExt.sendId
      };
      params.headerExtensions.push(ext);
    }
  } catch (err) {
    _didIteratorError8 = true;
    _iteratorError8 = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion8 && _iterator8.return != null) {
        _iterator8.return();
      }
    } finally {
      if (_didIteratorError8) {
        throw _iteratorError8;
      }
    }
  }

  return params;
}
/**
 * Generate RTP parameters of the given kind for receiving media.
 * All the media codecs per kind are considered. This is useful for generating
 * a SDP remote offer.
 * NOTE: muxId, encodings and rtcp fields are left empty.
 *
 * @param {String} kind
 * @param {RTCExtendedRtpCapabilities} extendedRtpCapabilities
 *
 * @return {RTCRtpParameters}
 */


function getReceivingFullRtpParameters(kind, extendedRtpCapabilities) {
  var params = {
    muxId: null,
    codecs: [],
    headerExtensions: [],
    encodings: [],
    rtcp: {}
  };
  var _iteratorNormalCompletion9 = true;
  var _didIteratorError9 = false;
  var _iteratorError9 = undefined;

  try {
    for (var _iterator9 = extendedRtpCapabilities.codecs[Symbol.iterator](), _step9; !(_iteratorNormalCompletion9 = (_step9 = _iterator9.next()).done); _iteratorNormalCompletion9 = true) {
      var capCodec = _step9.value;
      if (capCodec.kind !== kind) continue;
      var codec = {
        name: capCodec.name,
        mimeType: capCodec.mimeType,
        clockRate: capCodec.clockRate,
        payloadType: capCodec.recvPayloadType,
        channels: capCodec.channels,
        rtcpFeedback: capCodec.rtcpFeedback,
        parameters: capCodec.parameters
      };
      if (!codec.channels) delete codec.channels;
      params.codecs.push(codec); // Add RTX codec.

      if (capCodec.recvRtxPayloadType) {
        var rtxCodec = {
          name: 'rtx',
          mimeType: "".concat(capCodec.kind, "/rtx"),
          clockRate: capCodec.clockRate,
          payloadType: capCodec.recvRtxPayloadType,
          parameters: {
            apt: capCodec.recvPayloadType
          }
        };
        params.codecs.push(rtxCodec);
      } // TODO: In the future, we need to add FEC, CN, etc, codecs.

    }
  } catch (err) {
    _didIteratorError9 = true;
    _iteratorError9 = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion9 && _iterator9.return != null) {
        _iterator9.return();
      }
    } finally {
      if (_didIteratorError9) {
        throw _iteratorError9;
      }
    }
  }

  var _iteratorNormalCompletion10 = true;
  var _didIteratorError10 = false;
  var _iteratorError10 = undefined;

  try {
    for (var _iterator10 = extendedRtpCapabilities.headerExtensions[Symbol.iterator](), _step10; !(_iteratorNormalCompletion10 = (_step10 = _iterator10.next()).done); _iteratorNormalCompletion10 = true) {
      var capExt = _step10.value;
      if (capExt.kind && capExt.kind !== kind) continue;
      var ext = {
        uri: capExt.uri,
        id: capExt.recvId
      };
      params.headerExtensions.push(ext);
    }
  } catch (err) {
    _didIteratorError10 = true;
    _iteratorError10 = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion10 && _iterator10.return != null) {
        _iterator10.return();
      }
    } finally {
      if (_didIteratorError10) {
        throw _iteratorError10;
      }
    }
  }

  return params;
}

function matchCapCodecs(aCodec, bCodec) {
  var aMimeType = aCodec.mimeType.toLowerCase();
  var bMimeType = bCodec.mimeType.toLowerCase();
  if (aMimeType !== bMimeType) return false;
  if (aCodec.clockRate !== bCodec.clockRate) return false;
  if (aCodec.channels !== bCodec.channels) return false; // Match H264 parameters.

  if (aMimeType === 'video/h264') {
    var aPacketizationMode = (aCodec.parameters || {})['packetization-mode'] || 0;
    var bPacketizationMode = (bCodec.parameters || {})['packetization-mode'] || 0;
    if (aPacketizationMode !== bPacketizationMode) return false;
  }

  return true;
}

function matchCapHeaderExtensions(aExt, bExt) {
  if (aExt.kind && bExt.kind && aExt.kind !== bExt.kind) return false;
  if (aExt.uri !== bExt.uri) return false;
  return true;
}

function reduceRtcpFeedback(codecA, codecB) {
  var reducedRtcpFeedback = [];
  var _iteratorNormalCompletion11 = true;
  var _didIteratorError11 = false;
  var _iteratorError11 = undefined;

  try {
    var _loop5 = function _loop5() {
      var aFb = _step11.value;
      var matchingBFb = (codecB.rtcpFeedback || []).find(function (bFb) {
        return bFb.type === aFb.type && bFb.parameter === aFb.parameter;
      });
      if (matchingBFb) reducedRtcpFeedback.push(matchingBFb);
    };

    for (var _iterator11 = (codecA.rtcpFeedback || [])[Symbol.iterator](), _step11; !(_iteratorNormalCompletion11 = (_step11 = _iterator11.next()).done); _iteratorNormalCompletion11 = true) {
      _loop5();
    }
  } catch (err) {
    _didIteratorError11 = true;
    _iteratorError11 = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion11 && _iterator11.return != null) {
        _iterator11.return();
      }
    } finally {
      if (_didIteratorError11) {
        throw _iteratorError11;
      }
    }
  }

  return reducedRtcpFeedback;
}

/***/ }),

/***/ "./node_modules/mediasoup-client/lib-es5/utils.js":
/*!********************************************************!*\
  !*** ./node_modules/mediasoup-client/lib-es5/utils.js ***!
  \********************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.randomNumber = randomNumber;
exports.clone = clone;

var _randomNumber = _interopRequireDefault(__webpack_require__(/*! random-number */ "./node_modules/random-number/index.js"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var randomNumberGenerator = _randomNumber.default.generator({
  min: 10000000,
  max: 99999999,
  integer: true
});
/**
 * Generates a random positive number between 10000000 and 99999999.
 *
 * @return {Number}
 */


function randomNumber() {
  return randomNumberGenerator();
}
/**
 * Clones the given Object/Array.
 *
 * @param {Object|Array} obj
 *
 * @return {Object|Array}
 */


function clone(obj) {
  return JSON.parse(JSON.stringify(obj));
}

/***/ }),

/***/ "./node_modules/ms/index.js":
/*!**********************************!*\
  !*** ./node_modules/ms/index.js ***!
  \**********************************/
/*! no static exports found */
/***/ (function(module, exports) {

/**
 * Helpers.
 */

var s = 1000;
var m = s * 60;
var h = m * 60;
var d = h * 24;
var w = d * 7;
var y = d * 365.25;

/**
 * Parse or format the given `val`.
 *
 * Options:
 *
 *  - `long` verbose formatting [false]
 *
 * @param {String|Number} val
 * @param {Object} [options]
 * @throws {Error} throw an error if val is not a non-empty string or a number
 * @return {String|Number}
 * @api public
 */

module.exports = function(val, options) {
  options = options || {};
  var type = typeof val;
  if (type === 'string' && val.length > 0) {
    return parse(val);
  } else if (type === 'number' && isFinite(val)) {
    return options.long ? fmtLong(val) : fmtShort(val);
  }
  throw new Error(
    'val is not a non-empty string or a valid number. val=' +
      JSON.stringify(val)
  );
};

/**
 * Parse the given `str` and return milliseconds.
 *
 * @param {String} str
 * @return {Number}
 * @api private
 */

function parse(str) {
  str = String(str);
  if (str.length > 100) {
    return;
  }
  var match = /^(-?(?:\d+)?\.?\d+) *(milliseconds?|msecs?|ms|seconds?|secs?|s|minutes?|mins?|m|hours?|hrs?|h|days?|d|weeks?|w|years?|yrs?|y)?$/i.exec(
    str
  );
  if (!match) {
    return;
  }
  var n = parseFloat(match[1]);
  var type = (match[2] || 'ms').toLowerCase();
  switch (type) {
    case 'years':
    case 'year':
    case 'yrs':
    case 'yr':
    case 'y':
      return n * y;
    case 'weeks':
    case 'week':
    case 'w':
      return n * w;
    case 'days':
    case 'day':
    case 'd':
      return n * d;
    case 'hours':
    case 'hour':
    case 'hrs':
    case 'hr':
    case 'h':
      return n * h;
    case 'minutes':
    case 'minute':
    case 'mins':
    case 'min':
    case 'm':
      return n * m;
    case 'seconds':
    case 'second':
    case 'secs':
    case 'sec':
    case 's':
      return n * s;
    case 'milliseconds':
    case 'millisecond':
    case 'msecs':
    case 'msec':
    case 'ms':
      return n;
    default:
      return undefined;
  }
}

/**
 * Short format for `ms`.
 *
 * @param {Number} ms
 * @return {String}
 * @api private
 */

function fmtShort(ms) {
  var msAbs = Math.abs(ms);
  if (msAbs >= d) {
    return Math.round(ms / d) + 'd';
  }
  if (msAbs >= h) {
    return Math.round(ms / h) + 'h';
  }
  if (msAbs >= m) {
    return Math.round(ms / m) + 'm';
  }
  if (msAbs >= s) {
    return Math.round(ms / s) + 's';
  }
  return ms + 'ms';
}

/**
 * Long format for `ms`.
 *
 * @param {Number} ms
 * @return {String}
 * @api private
 */

function fmtLong(ms) {
  var msAbs = Math.abs(ms);
  if (msAbs >= d) {
    return plural(ms, msAbs, d, 'day');
  }
  if (msAbs >= h) {
    return plural(ms, msAbs, h, 'hour');
  }
  if (msAbs >= m) {
    return plural(ms, msAbs, m, 'minute');
  }
  if (msAbs >= s) {
    return plural(ms, msAbs, s, 'second');
  }
  return ms + ' ms';
}

/**
 * Pluralization helper.
 */

function plural(ms, msAbs, n, name) {
  var isPlural = msAbs >= n * 1.5;
  return Math.round(ms / n) + ' ' + name + (isPlural ? 's' : '');
}


/***/ }),

/***/ "./node_modules/parseqs/index.js":
/*!***************************************!*\
  !*** ./node_modules/parseqs/index.js ***!
  \***************************************/
/*! no static exports found */
/***/ (function(module, exports) {

/**
 * Compiles a querystring
 * Returns string representation of the object
 *
 * @param {Object}
 * @api private
 */

exports.encode = function (obj) {
  var str = '';

  for (var i in obj) {
    if (obj.hasOwnProperty(i)) {
      if (str.length) str += '&';
      str += encodeURIComponent(i) + '=' + encodeURIComponent(obj[i]);
    }
  }

  return str;
};

/**
 * Parses a simple querystring into an object
 *
 * @param {String} qs
 * @api private
 */

exports.decode = function(qs){
  var qry = {};
  var pairs = qs.split('&');
  for (var i = 0, l = pairs.length; i < l; i++) {
    var pair = pairs[i].split('=');
    qry[decodeURIComponent(pair[0])] = decodeURIComponent(pair[1]);
  }
  return qry;
};


/***/ }),

/***/ "./node_modules/parseuri/index.js":
/*!****************************************!*\
  !*** ./node_modules/parseuri/index.js ***!
  \****************************************/
/*! no static exports found */
/***/ (function(module, exports) {

/**
 * Parses an URI
 *
 * @author Steven Levithan <stevenlevithan.com> (MIT license)
 * @api private
 */

var re = /^(?:(?![^:@]+:[^:@\/]*@)(http|https|ws|wss):\/\/)?((?:(([^:@]*)(?::([^:@]*))?)?@)?((?:[a-f0-9]{0,4}:){2,7}[a-f0-9]{0,4}|[^:\/?#]*)(?::(\d*))?)(((\/(?:[^?#](?![^?#\/]*\.[^?#\/.]+(?:[?#]|$)))*\/?)?([^?#\/]*))(?:\?([^#]*))?(?:#(.*))?)/;

var parts = [
    'source', 'protocol', 'authority', 'userInfo', 'user', 'password', 'host', 'port', 'relative', 'path', 'directory', 'file', 'query', 'anchor'
];

module.exports = function parseuri(str) {
    var src = str,
        b = str.indexOf('['),
        e = str.indexOf(']');

    if (b != -1 && e != -1) {
        str = str.substring(0, b) + str.substring(b, e).replace(/:/g, ';') + str.substring(e, str.length);
    }

    var m = re.exec(str || ''),
        uri = {},
        i = 14;

    while (i--) {
        uri[parts[i]] = m[i] || '';
    }

    if (b != -1 && e != -1) {
        uri.source = src;
        uri.host = uri.host.substring(1, uri.host.length - 1).replace(/;/g, ':');
        uri.authority = uri.authority.replace('[', '').replace(']', '').replace(/;/g, ':');
        uri.ipv6uri = true;
    }

    return uri;
};


/***/ }),

/***/ "./node_modules/process/browser.js":
/*!*****************************************!*\
  !*** ./node_modules/process/browser.js ***!
  \*****************************************/
/*! no static exports found */
/***/ (function(module, exports) {

// shim for using process in browser
var process = module.exports = {};

// cached from whatever global is present so that test runners that stub it
// don't break things.  But we need to wrap it in a try catch in case it is
// wrapped in strict mode code which doesn't define any globals.  It's inside a
// function because try/catches deoptimize in certain engines.

var cachedSetTimeout;
var cachedClearTimeout;

function defaultSetTimout() {
    throw new Error('setTimeout has not been defined');
}
function defaultClearTimeout () {
    throw new Error('clearTimeout has not been defined');
}
(function () {
    try {
        if (typeof setTimeout === 'function') {
            cachedSetTimeout = setTimeout;
        } else {
            cachedSetTimeout = defaultSetTimout;
        }
    } catch (e) {
        cachedSetTimeout = defaultSetTimout;
    }
    try {
        if (typeof clearTimeout === 'function') {
            cachedClearTimeout = clearTimeout;
        } else {
            cachedClearTimeout = defaultClearTimeout;
        }
    } catch (e) {
        cachedClearTimeout = defaultClearTimeout;
    }
} ())
function runTimeout(fun) {
    if (cachedSetTimeout === setTimeout) {
        //normal enviroments in sane situations
        return setTimeout(fun, 0);
    }
    // if setTimeout wasn't available but was latter defined
    if ((cachedSetTimeout === defaultSetTimout || !cachedSetTimeout) && setTimeout) {
        cachedSetTimeout = setTimeout;
        return setTimeout(fun, 0);
    }
    try {
        // when when somebody has screwed with setTimeout but no I.E. maddness
        return cachedSetTimeout(fun, 0);
    } catch(e){
        try {
            // When we are in I.E. but the script has been evaled so I.E. doesn't trust the global object when called normally
            return cachedSetTimeout.call(null, fun, 0);
        } catch(e){
            // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error
            return cachedSetTimeout.call(this, fun, 0);
        }
    }


}
function runClearTimeout(marker) {
    if (cachedClearTimeout === clearTimeout) {
        //normal enviroments in sane situations
        return clearTimeout(marker);
    }
    // if clearTimeout wasn't available but was latter defined
    if ((cachedClearTimeout === defaultClearTimeout || !cachedClearTimeout) && clearTimeout) {
        cachedClearTimeout = clearTimeout;
        return clearTimeout(marker);
    }
    try {
        // when when somebody has screwed with setTimeout but no I.E. maddness
        return cachedClearTimeout(marker);
    } catch (e){
        try {
            // When we are in I.E. but the script has been evaled so I.E. doesn't  trust the global object when called normally
            return cachedClearTimeout.call(null, marker);
        } catch (e){
            // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error.
            // Some versions of I.E. have different rules for clearTimeout vs setTimeout
            return cachedClearTimeout.call(this, marker);
        }
    }



}
var queue = [];
var draining = false;
var currentQueue;
var queueIndex = -1;

function cleanUpNextTick() {
    if (!draining || !currentQueue) {
        return;
    }
    draining = false;
    if (currentQueue.length) {
        queue = currentQueue.concat(queue);
    } else {
        queueIndex = -1;
    }
    if (queue.length) {
        drainQueue();
    }
}

function drainQueue() {
    if (draining) {
        return;
    }
    var timeout = runTimeout(cleanUpNextTick);
    draining = true;

    var len = queue.length;
    while(len) {
        currentQueue = queue;
        queue = [];
        while (++queueIndex < len) {
            if (currentQueue) {
                currentQueue[queueIndex].run();
            }
        }
        queueIndex = -1;
        len = queue.length;
    }
    currentQueue = null;
    draining = false;
    runClearTimeout(timeout);
}

process.nextTick = function (fun) {
    var args = new Array(arguments.length - 1);
    if (arguments.length > 1) {
        for (var i = 1; i < arguments.length; i++) {
            args[i - 1] = arguments[i];
        }
    }
    queue.push(new Item(fun, args));
    if (queue.length === 1 && !draining) {
        runTimeout(drainQueue);
    }
};

// v8 likes predictible objects
function Item(fun, array) {
    this.fun = fun;
    this.array = array;
}
Item.prototype.run = function () {
    this.fun.apply(null, this.array);
};
process.title = 'browser';
process.browser = true;
process.env = {};
process.argv = [];
process.version = ''; // empty string to avoid regexp issues
process.versions = {};

function noop() {}

process.on = noop;
process.addListener = noop;
process.once = noop;
process.off = noop;
process.removeListener = noop;
process.removeAllListeners = noop;
process.emit = noop;
process.prependListener = noop;
process.prependOnceListener = noop;

process.listeners = function (name) { return [] }

process.binding = function (name) {
    throw new Error('process.binding is not supported');
};

process.cwd = function () { return '/' };
process.chdir = function (dir) {
    throw new Error('process.chdir is not supported');
};
process.umask = function() { return 0; };


/***/ }),

/***/ "./node_modules/random-number/index.js":
/*!*********************************************!*\
  !*** ./node_modules/random-number/index.js ***!
  \*********************************************/
/*! no static exports found */
/***/ (function(module, exports) {

void function(root){

  function defaults(options){
    var options = options || {}
    var min = options.min
    var max = options.max
    var integer = options.integer || false
    if ( min == null && max == null ) {
      min = 0
      max = 1
    } else if ( min == null ) {
      min = max - 1
    } else if ( max == null ) {
      max = min + 1
    }
    if ( max < min ) throw new Error('invalid options, max must be >= min')
    return {
      min:     min
    , max:     max
    , integer: integer
    }
  }

  function random(options){
    options = defaults(options)
    if ( options.max === options.min ) return options.min
    var r = Math.random() * (options.max - options.min + Number(!!options.integer)) + options.min
    return options.integer ? Math.floor(r) : r
  }

  function generator(options){
    options = defaults(options)
    return function(min, max, integer){
      options.min     = min != null ? min : options.min
      options.max     = max != null ? max : options.max
      options.integer = integer != null ? integer : options.integer
      return random(options)
    }
  }

  module.exports =  random
  module.exports.generator = generator
  module.exports.defaults = defaults
}(this)


/***/ }),

/***/ "./node_modules/sdp-transform/lib/grammar.js":
/*!***************************************************!*\
  !*** ./node_modules/sdp-transform/lib/grammar.js ***!
  \***************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

var grammar = module.exports = {
  v: [{
    name: 'version',
    reg: /^(\d*)$/
  }],
  o: [{
    // o=- 20518 0 IN IP4 203.0.113.1
    // NB: sessionId will be a String in most cases because it is huge
    name: 'origin',
    reg: /^(\S*) (\d*) (\d*) (\S*) IP(\d) (\S*)/,
    names: ['username', 'sessionId', 'sessionVersion', 'netType', 'ipVer', 'address'],
    format: '%s %s %d %s IP%d %s'
  }],
  // default parsing of these only (though some of these feel outdated)
  s: [{ name: 'name' }],
  i: [{ name: 'description' }],
  u: [{ name: 'uri' }],
  e: [{ name: 'email' }],
  p: [{ name: 'phone' }],
  z: [{ name: 'timezones' }], // TODO: this one can actually be parsed properly...
  r: [{ name: 'repeats' }],   // TODO: this one can also be parsed properly
  // k: [{}], // outdated thing ignored
  t: [{
    // t=0 0
    name: 'timing',
    reg: /^(\d*) (\d*)/,
    names: ['start', 'stop'],
    format: '%d %d'
  }],
  c: [{
    // c=IN IP4 10.47.197.26
    name: 'connection',
    reg: /^IN IP(\d) (\S*)/,
    names: ['version', 'ip'],
    format: 'IN IP%d %s'
  }],
  b: [{
    // b=AS:4000
    push: 'bandwidth',
    reg: /^(TIAS|AS|CT|RR|RS):(\d*)/,
    names: ['type', 'limit'],
    format: '%s:%s'
  }],
  m: [{
    // m=video 51744 RTP/AVP 126 97 98 34 31
    // NB: special - pushes to session
    // TODO: rtp/fmtp should be filtered by the payloads found here?
    reg: /^(\w*) (\d*) ([\w/]*)(?: (.*))?/,
    names: ['type', 'port', 'protocol', 'payloads'],
    format: '%s %d %s %s'
  }],
  a: [
    {
      // a=rtpmap:110 opus/48000/2
      push: 'rtp',
      reg: /^rtpmap:(\d*) ([\w\-.]*)(?:\s*\/(\d*)(?:\s*\/(\S*))?)?/,
      names: ['payload', 'codec', 'rate', 'encoding'],
      format: function (o) {
        return (o.encoding)
          ? 'rtpmap:%d %s/%s/%s'
          : o.rate
            ? 'rtpmap:%d %s/%s'
            : 'rtpmap:%d %s';
      }
    },
    {
      // a=fmtp:108 profile-level-id=24;object=23;bitrate=64000
      // a=fmtp:111 minptime=10; useinbandfec=1
      push: 'fmtp',
      reg: /^fmtp:(\d*) ([\S| ]*)/,
      names: ['payload', 'config'],
      format: 'fmtp:%d %s'
    },
    {
      // a=control:streamid=0
      name: 'control',
      reg: /^control:(.*)/,
      format: 'control:%s'
    },
    {
      // a=rtcp:65179 IN IP4 193.84.77.194
      name: 'rtcp',
      reg: /^rtcp:(\d*)(?: (\S*) IP(\d) (\S*))?/,
      names: ['port', 'netType', 'ipVer', 'address'],
      format: function (o) {
        return (o.address != null)
          ? 'rtcp:%d %s IP%d %s'
          : 'rtcp:%d';
      }
    },
    {
      // a=rtcp-fb:98 trr-int 100
      push: 'rtcpFbTrrInt',
      reg: /^rtcp-fb:(\*|\d*) trr-int (\d*)/,
      names: ['payload', 'value'],
      format: 'rtcp-fb:%d trr-int %d'
    },
    {
      // a=rtcp-fb:98 nack rpsi
      push: 'rtcpFb',
      reg: /^rtcp-fb:(\*|\d*) ([\w-_]*)(?: ([\w-_]*))?/,
      names: ['payload', 'type', 'subtype'],
      format: function (o) {
        return (o.subtype != null)
          ? 'rtcp-fb:%s %s %s'
          : 'rtcp-fb:%s %s';
      }
    },
    {
      // a=extmap:2 urn:ietf:params:rtp-hdrext:toffset
      // a=extmap:1/recvonly URI-gps-string
      // a=extmap:3 urn:ietf:params:rtp-hdrext:encrypt urn:ietf:params:rtp-hdrext:smpte-tc 25@600/24
      push: 'ext',
      reg: /^extmap:(\d+)(?:\/(\w+))?(?: (urn:ietf:params:rtp-hdrext:encrypt))? (\S*)(?: (\S*))?/,
      names: ['value', 'direction', 'encrypt-uri', 'uri', 'config'],
      format: function (o) {
        return (
          'extmap:%d' +
          (o.direction ? '/%s' : '%v') +
          (o['encrypt-uri'] ? ' %s' : '%v') +
          ' %s' +
          (o.config ? ' %s' : '')
        );
      }
    },
    {
      // a=extmap-allow-mixed
      name: 'extmapAllowMixed',
      reg: /^(extmap-allow-mixed)/
    },
    {
      // a=crypto:1 AES_CM_128_HMAC_SHA1_80 inline:PS1uQCVeeCFCanVmcjkpPywjNWhcYD0mXXtxaVBR|2^20|1:32
      push: 'crypto',
      reg: /^crypto:(\d*) ([\w_]*) (\S*)(?: (\S*))?/,
      names: ['id', 'suite', 'config', 'sessionConfig'],
      format: function (o) {
        return (o.sessionConfig != null)
          ? 'crypto:%d %s %s %s'
          : 'crypto:%d %s %s';
      }
    },
    {
      // a=setup:actpass
      name: 'setup',
      reg: /^setup:(\w*)/,
      format: 'setup:%s'
    },
    {
      // a=connection:new
      name: 'connectionType',
      reg: /^connection:(new|existing)/,
      format: 'connection:%s'
    },
    {
      // a=mid:1
      name: 'mid',
      reg: /^mid:([^\s]*)/,
      format: 'mid:%s'
    },
    {
      // a=msid:0c8b064d-d807-43b4-b434-f92a889d8587 98178685-d409-46e0-8e16-7ef0db0db64a
      name: 'msid',
      reg: /^msid:(.*)/,
      format: 'msid:%s'
    },
    {
      // a=ptime:20
      name: 'ptime',
      reg: /^ptime:(\d*)/,
      format: 'ptime:%d'
    },
    {
      // a=maxptime:60
      name: 'maxptime',
      reg: /^maxptime:(\d*)/,
      format: 'maxptime:%d'
    },
    {
      // a=sendrecv
      name: 'direction',
      reg: /^(sendrecv|recvonly|sendonly|inactive)/
    },
    {
      // a=ice-lite
      name: 'icelite',
      reg: /^(ice-lite)/
    },
    {
      // a=ice-ufrag:F7gI
      name: 'iceUfrag',
      reg: /^ice-ufrag:(\S*)/,
      format: 'ice-ufrag:%s'
    },
    {
      // a=ice-pwd:x9cml/YzichV2+XlhiMu8g
      name: 'icePwd',
      reg: /^ice-pwd:(\S*)/,
      format: 'ice-pwd:%s'
    },
    {
      // a=fingerprint:SHA-1 00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD:EE:FF:00:11:22:33
      name: 'fingerprint',
      reg: /^fingerprint:(\S*) (\S*)/,
      names: ['type', 'hash'],
      format: 'fingerprint:%s %s'
    },
    {
      // a=candidate:0 1 UDP 2113667327 203.0.113.1 54400 typ host
      // a=candidate:1162875081 1 udp 2113937151 192.168.34.75 60017 typ host generation 0 network-id 3 network-cost 10
      // a=candidate:3289912957 2 udp 1845501695 193.84.77.194 60017 typ srflx raddr 192.168.34.75 rport 60017 generation 0 network-id 3 network-cost 10
      // a=candidate:229815620 1 tcp 1518280447 192.168.150.19 60017 typ host tcptype active generation 0 network-id 3 network-cost 10
      // a=candidate:3289912957 2 tcp 1845501695 193.84.77.194 60017 typ srflx raddr 192.168.34.75 rport 60017 tcptype passive generation 0 network-id 3 network-cost 10
      push:'candidates',
      reg: /^candidate:(\S*) (\d*) (\S*) (\d*) (\S*) (\d*) typ (\S*)(?: raddr (\S*) rport (\d*))?(?: tcptype (\S*))?(?: generation (\d*))?(?: network-id (\d*))?(?: network-cost (\d*))?/,
      names: ['foundation', 'component', 'transport', 'priority', 'ip', 'port', 'type', 'raddr', 'rport', 'tcptype', 'generation', 'network-id', 'network-cost'],
      format: function (o) {
        var str = 'candidate:%s %d %s %d %s %d typ %s';

        str += (o.raddr != null) ? ' raddr %s rport %d' : '%v%v';

        // NB: candidate has three optional chunks, so %void middles one if it's missing
        str += (o.tcptype != null) ? ' tcptype %s' : '%v';

        if (o.generation != null) {
          str += ' generation %d';
        }

        str += (o['network-id'] != null) ? ' network-id %d' : '%v';
        str += (o['network-cost'] != null) ? ' network-cost %d' : '%v';
        return str;
      }
    },
    {
      // a=end-of-candidates (keep after the candidates line for readability)
      name: 'endOfCandidates',
      reg: /^(end-of-candidates)/
    },
    {
      // a=remote-candidates:1 203.0.113.1 54400 2 203.0.113.1 54401 ...
      name: 'remoteCandidates',
      reg: /^remote-candidates:(.*)/,
      format: 'remote-candidates:%s'
    },
    {
      // a=ice-options:google-ice
      name: 'iceOptions',
      reg: /^ice-options:(\S*)/,
      format: 'ice-options:%s'
    },
    {
      // a=ssrc:2566107569 cname:t9YU8M1UxTF8Y1A1
      push: 'ssrcs',
      reg: /^ssrc:(\d*) ([\w_-]*)(?::(.*))?/,
      names: ['id', 'attribute', 'value'],
      format: function (o) {
        var str = 'ssrc:%d';
        if (o.attribute != null) {
          str += ' %s';
          if (o.value != null) {
            str += ':%s';
          }
        }
        return str;
      }
    },
    {
      // a=ssrc-group:FEC 1 2
      // a=ssrc-group:FEC-FR 3004364195 1080772241
      push: 'ssrcGroups',
      // token-char = %x21 / %x23-27 / %x2A-2B / %x2D-2E / %x30-39 / %x41-5A / %x5E-7E
      reg: /^ssrc-group:([\x21\x23\x24\x25\x26\x27\x2A\x2B\x2D\x2E\w]*) (.*)/,
      names: ['semantics', 'ssrcs'],
      format: 'ssrc-group:%s %s'
    },
    {
      // a=msid-semantic: WMS Jvlam5X3SX1OP6pn20zWogvaKJz5Hjf9OnlV
      name: 'msidSemantic',
      reg: /^msid-semantic:\s?(\w*) (\S*)/,
      names: ['semantic', 'token'],
      format: 'msid-semantic: %s %s' // space after ':' is not accidental
    },
    {
      // a=group:BUNDLE audio video
      push: 'groups',
      reg: /^group:(\w*) (.*)/,
      names: ['type', 'mids'],
      format: 'group:%s %s'
    },
    {
      // a=rtcp-mux
      name: 'rtcpMux',
      reg: /^(rtcp-mux)/
    },
    {
      // a=rtcp-rsize
      name: 'rtcpRsize',
      reg: /^(rtcp-rsize)/
    },
    {
      // a=sctpmap:5000 webrtc-datachannel 1024
      name: 'sctpmap',
      reg: /^sctpmap:([\w_/]*) (\S*)(?: (\S*))?/,
      names: ['sctpmapNumber', 'app', 'maxMessageSize'],
      format: function (o) {
        return (o.maxMessageSize != null)
          ? 'sctpmap:%s %s %s'
          : 'sctpmap:%s %s';
      }
    },
    {
      // a=x-google-flag:conference
      name: 'xGoogleFlag',
      reg: /^x-google-flag:([^\s]*)/,
      format: 'x-google-flag:%s'
    },
    {
      // a=rid:1 send max-width=1280;max-height=720;max-fps=30;depend=0
      push: 'rids',
      reg: /^rid:([\d\w]+) (\w+)(?: ([\S| ]*))?/,
      names: ['id', 'direction', 'params'],
      format: function (o) {
        return (o.params) ? 'rid:%s %s %s' : 'rid:%s %s';
      }
    },
    {
      // a=imageattr:97 send [x=800,y=640,sar=1.1,q=0.6] [x=480,y=320] recv [x=330,y=250]
      // a=imageattr:* send [x=800,y=640] recv *
      // a=imageattr:100 recv [x=320,y=240]
      push: 'imageattrs',
      reg: new RegExp(
        // a=imageattr:97
        '^imageattr:(\\d+|\\*)' +
        // send [x=800,y=640,sar=1.1,q=0.6] [x=480,y=320]
        '[\\s\\t]+(send|recv)[\\s\\t]+(\\*|\\[\\S+\\](?:[\\s\\t]+\\[\\S+\\])*)' +
        // recv [x=330,y=250]
        '(?:[\\s\\t]+(recv|send)[\\s\\t]+(\\*|\\[\\S+\\](?:[\\s\\t]+\\[\\S+\\])*))?'
      ),
      names: ['pt', 'dir1', 'attrs1', 'dir2', 'attrs2'],
      format: function (o) {
        return 'imageattr:%s %s %s' + (o.dir2 ? ' %s %s' : '');
      }
    },
    {
      // a=simulcast:send 1,2,3;~4,~5 recv 6;~7,~8
      // a=simulcast:recv 1;4,5 send 6;7
      name: 'simulcast',
      reg: new RegExp(
        // a=simulcast:
        '^simulcast:' +
        // send 1,2,3;~4,~5
        '(send|recv) ([a-zA-Z0-9\\-_~;,]+)' +
        // space + recv 6;~7,~8
        '(?:\\s?(send|recv) ([a-zA-Z0-9\\-_~;,]+))?' +
        // end
        '$'
      ),
      names: ['dir1', 'list1', 'dir2', 'list2'],
      format: function (o) {
        return 'simulcast:%s %s' + (o.dir2 ? ' %s %s' : '');
      }
    },
    {
      // old simulcast draft 03 (implemented by Firefox)
      //   https://tools.ietf.org/html/draft-ietf-mmusic-sdp-simulcast-03
      // a=simulcast: recv pt=97;98 send pt=97
      // a=simulcast: send rid=5;6;7 paused=6,7
      name: 'simulcast_03',
      reg: /^simulcast:[\s\t]+([\S+\s\t]+)$/,
      names: ['value'],
      format: 'simulcast: %s'
    },
    {
      // a=framerate:25
      // a=framerate:29.97
      name: 'framerate',
      reg: /^framerate:(\d+(?:$|\.\d+))/,
      format: 'framerate:%s'
    },
    {
      // RFC4570
      // a=source-filter: incl IN IP4 239.5.2.31 10.1.15.5
      name: 'sourceFilter',
      reg: /^source-filter: *(excl|incl) (\S*) (IP4|IP6|\*) (\S*) (.*)/,
      names: ['filterMode', 'netType', 'addressTypes', 'destAddress', 'srcList'],
      format: 'source-filter: %s %s %s %s %s'
    },
    {
      // a=bundle-only
      name: 'bundleOnly',
      reg: /^(bundle-only)/
    },
    {
      // a=label:1
      name: 'label',
      reg: /^label:(.+)/,
      format: 'label:%s'
    },
    {
      // RFC version 26 for SCTP over DTLS
      // https://tools.ietf.org/html/draft-ietf-mmusic-sctp-sdp-26#section-5
      name: 'sctpPort',
      reg: /^sctp-port:(\d+)$/,
      format: 'sctp-port:%s'
    },
    {
      // RFC version 26 for SCTP over DTLS
      // https://tools.ietf.org/html/draft-ietf-mmusic-sctp-sdp-26#section-6
      name: 'maxMessageSize',
      reg: /^max-message-size:(\d+)$/,
      format: 'max-message-size:%s'
    },
    {
      // RFC7273
      // a=ts-refclk:ptp=IEEE1588-2008:39-A7-94-FF-FE-07-CB-D0:37
      push:'tsRefClocks',
      reg: /^ts-refclk:([^\s=]*)(?:=(\S*))?/,
      names: ['clksrc', 'clksrcExt'],
      format: function (o) {
        return 'ts-refclk:%s' + (o.clksrcExt != null ? '=%s' : '');
      }
    },
    {
      // RFC7273
      // a=mediaclk:direct=963214424
      name:'mediaClk',
      reg: /^mediaclk:(?:id=(\S*))? *([^\s=]*)(?:=(\S*))?(?: *rate=(\d+)\/(\d+))?/,
      names: ['id', 'mediaClockName', 'mediaClockValue', 'rateNumerator', 'rateDenominator'],
      format: function (o) {
        var str = 'mediaclk:';
        str += (o.id != null ? 'id=%s %s' : '%v%s');
        str += (o.mediaClockValue != null ? '=%s' : '');
        str += (o.rateNumerator != null ? ' rate=%s' : '');
        str += (o.rateDenominator != null ? '/%s' : '');
        return str;
      }
    },
    {
      // a=keywds:keywords
      name: 'keywords',
      reg: /^keywds:(.+)$/,
      format: 'keywds:%s'
    },
    {
      // a=content:main
      name: 'content',
      reg: /^content:(.+)/,
      format: 'content:%s'
    },
    // BFCP https://tools.ietf.org/html/rfc4583
    {
      // a=floorctrl:c-s
      name: 'bfcpFloorCtrl',
      reg: /^floorctrl:(c-only|s-only|c-s)/,
      format: 'floorctrl:%s'
    },
    {
      // a=confid:1
      name: 'bfcpConfId',
      reg: /^confid:(\d+)/,
      format: 'confid:%s'
    },
    {
      // a=userid:1
      name: 'bfcpUserId',
      reg: /^userid:(\d+)/,
      format: 'userid:%s'
    },
    {
      // a=floorid:1
      name: 'bfcpFloorId',
      reg: /^floorid:(.+) (?:m-stream|mstrm):(.+)/,
      names: ['id', 'mStream'],
      format: 'floorid:%s mstrm:%s'
    },
    {
      // any a= that we don't understand is kept verbatim on media.invalid
      push: 'invalid',
      names: ['value']
    }
  ]
};

// set sensible defaults to avoid polluting the grammar with boring details
Object.keys(grammar).forEach(function (key) {
  var objs = grammar[key];
  objs.forEach(function (obj) {
    if (!obj.reg) {
      obj.reg = /(.*)/;
    }
    if (!obj.format) {
      obj.format = '%s';
    }
  });
});


/***/ }),

/***/ "./node_modules/sdp-transform/lib/index.js":
/*!*************************************************!*\
  !*** ./node_modules/sdp-transform/lib/index.js ***!
  \*************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var parser = __webpack_require__(/*! ./parser */ "./node_modules/sdp-transform/lib/parser.js");
var writer = __webpack_require__(/*! ./writer */ "./node_modules/sdp-transform/lib/writer.js");

exports.write = writer;
exports.parse = parser.parse;
exports.parseFmtpConfig = parser.parseFmtpConfig;
exports.parseParams = parser.parseParams;
exports.parsePayloads = parser.parsePayloads;
exports.parseRemoteCandidates = parser.parseRemoteCandidates;
exports.parseImageAttributes = parser.parseImageAttributes;
exports.parseSimulcastStreamList = parser.parseSimulcastStreamList;


/***/ }),

/***/ "./node_modules/sdp-transform/lib/parser.js":
/*!**************************************************!*\
  !*** ./node_modules/sdp-transform/lib/parser.js ***!
  \**************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var toIntIfInt = function (v) {
  return String(Number(v)) === v ? Number(v) : v;
};

var attachProperties = function (match, location, names, rawName) {
  if (rawName && !names) {
    location[rawName] = toIntIfInt(match[1]);
  }
  else {
    for (var i = 0; i < names.length; i += 1) {
      if (match[i+1] != null) {
        location[names[i]] = toIntIfInt(match[i+1]);
      }
    }
  }
};

var parseReg = function (obj, location, content) {
  var needsBlank = obj.name && obj.names;
  if (obj.push && !location[obj.push]) {
    location[obj.push] = [];
  }
  else if (needsBlank && !location[obj.name]) {
    location[obj.name] = {};
  }
  var keyLocation = obj.push ?
    {} :  // blank object that will be pushed
    needsBlank ? location[obj.name] : location; // otherwise, named location or root

  attachProperties(content.match(obj.reg), keyLocation, obj.names, obj.name);

  if (obj.push) {
    location[obj.push].push(keyLocation);
  }
};

var grammar = __webpack_require__(/*! ./grammar */ "./node_modules/sdp-transform/lib/grammar.js");
var validLine = RegExp.prototype.test.bind(/^([a-z])=(.*)/);

exports.parse = function (sdp) {
  var session = {}
    , media = []
    , location = session; // points at where properties go under (one of the above)

  // parse lines we understand
  sdp.split(/(\r\n|\r|\n)/).filter(validLine).forEach(function (l) {
    var type = l[0];
    var content = l.slice(2);
    if (type === 'm') {
      media.push({rtp: [], fmtp: []});
      location = media[media.length-1]; // point at latest media line
    }

    for (var j = 0; j < (grammar[type] || []).length; j += 1) {
      var obj = grammar[type][j];
      if (obj.reg.test(content)) {
        return parseReg(obj, location, content);
      }
    }
  });

  session.media = media; // link it up
  return session;
};

var paramReducer = function (acc, expr) {
  var s = expr.split(/=(.+)/, 2);
  if (s.length === 2) {
    acc[s[0]] = toIntIfInt(s[1]);
  } else if (s.length === 1 && expr.length > 1) {
    acc[s[0]] = undefined;
  }
  return acc;
};

exports.parseParams = function (str) {
  return str.split(/;\s?/).reduce(paramReducer, {});
};

// For backward compatibility - alias will be removed in 3.0.0
exports.parseFmtpConfig = exports.parseParams;

exports.parsePayloads = function (str) {
  return str.toString().split(' ').map(Number);
};

exports.parseRemoteCandidates = function (str) {
  var candidates = [];
  var parts = str.split(' ').map(toIntIfInt);
  for (var i = 0; i < parts.length; i += 3) {
    candidates.push({
      component: parts[i],
      ip: parts[i + 1],
      port: parts[i + 2]
    });
  }
  return candidates;
};

exports.parseImageAttributes = function (str) {
  return str.split(' ').map(function (item) {
    return item.substring(1, item.length-1).split(',').reduce(paramReducer, {});
  });
};

exports.parseSimulcastStreamList = function (str) {
  return str.split(';').map(function (stream) {
    return stream.split(',').map(function (format) {
      var scid, paused = false;

      if (format[0] !== '~') {
        scid = toIntIfInt(format);
      } else {
        scid = toIntIfInt(format.substring(1, format.length));
        paused = true;
      }

      return {
        scid: scid,
        paused: paused
      };
    });
  });
};


/***/ }),

/***/ "./node_modules/sdp-transform/lib/writer.js":
/*!**************************************************!*\
  !*** ./node_modules/sdp-transform/lib/writer.js ***!
  \**************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var grammar = __webpack_require__(/*! ./grammar */ "./node_modules/sdp-transform/lib/grammar.js");

// customized util.format - discards excess arguments and can void middle ones
var formatRegExp = /%[sdv%]/g;
var format = function (formatStr) {
  var i = 1;
  var args = arguments;
  var len = args.length;
  return formatStr.replace(formatRegExp, function (x) {
    if (i >= len) {
      return x; // missing argument
    }
    var arg = args[i];
    i += 1;
    switch (x) {
    case '%%':
      return '%';
    case '%s':
      return String(arg);
    case '%d':
      return Number(arg);
    case '%v':
      return '';
    }
  });
  // NB: we discard excess arguments - they are typically undefined from makeLine
};

var makeLine = function (type, obj, location) {
  var str = obj.format instanceof Function ?
    (obj.format(obj.push ? location : location[obj.name])) :
    obj.format;

  var args = [type + '=' + str];
  if (obj.names) {
    for (var i = 0; i < obj.names.length; i += 1) {
      var n = obj.names[i];
      if (obj.name) {
        args.push(location[obj.name][n]);
      }
      else { // for mLine and push attributes
        args.push(location[obj.names[i]]);
      }
    }
  }
  else {
    args.push(location[obj.name]);
  }
  return format.apply(null, args);
};

// RFC specified order
// TODO: extend this with all the rest
var defaultOuterOrder = [
  'v', 'o', 's', 'i',
  'u', 'e', 'p', 'c',
  'b', 't', 'r', 'z', 'a'
];
var defaultInnerOrder = ['i', 'c', 'b', 'a'];


module.exports = function (session, opts) {
  opts = opts || {};
  // ensure certain properties exist
  if (session.version == null) {
    session.version = 0; // 'v=0' must be there (only defined version atm)
  }
  if (session.name == null) {
    session.name = ' '; // 's= ' must be there if no meaningful name set
  }
  session.media.forEach(function (mLine) {
    if (mLine.payloads == null) {
      mLine.payloads = '';
    }
  });

  var outerOrder = opts.outerOrder || defaultOuterOrder;
  var innerOrder = opts.innerOrder || defaultInnerOrder;
  var sdp = [];

  // loop through outerOrder for matching properties on session
  outerOrder.forEach(function (type) {
    grammar[type].forEach(function (obj) {
      if (obj.name in session && session[obj.name] != null) {
        sdp.push(makeLine(type, obj, session));
      }
      else if (obj.push in session && session[obj.push] != null) {
        session[obj.push].forEach(function (el) {
          sdp.push(makeLine(type, obj, el));
        });
      }
    });
  });

  // then for each media line, follow the innerOrder
  session.media.forEach(function (mLine) {
    sdp.push(makeLine('m', grammar.m[0], mLine));

    innerOrder.forEach(function (type) {
      grammar[type].forEach(function (obj) {
        if (obj.name in mLine && mLine[obj.name] != null) {
          sdp.push(makeLine(type, obj, mLine));
        }
        else if (obj.push in mLine && mLine[obj.push] != null) {
          mLine[obj.push].forEach(function (el) {
            sdp.push(makeLine(type, obj, el));
          });
        }
      });
    });
  });

  return sdp.join('\r\n') + '\r\n';
};


/***/ }),

/***/ "./node_modules/socket.io-client/lib/index.js":
/*!****************************************************!*\
  !*** ./node_modules/socket.io-client/lib/index.js ***!
  \****************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {


/**
 * Module dependencies.
 */

var url = __webpack_require__(/*! ./url */ "./node_modules/socket.io-client/lib/url.js");
var parser = __webpack_require__(/*! socket.io-parser */ "./node_modules/socket.io-parser/index.js");
var Manager = __webpack_require__(/*! ./manager */ "./node_modules/socket.io-client/lib/manager.js");
var debug = __webpack_require__(/*! debug */ "./node_modules/debug/src/browser.js")('socket.io-client');

/**
 * Module exports.
 */

module.exports = exports = lookup;

/**
 * Managers cache.
 */

var cache = exports.managers = {};

/**
 * Looks up an existing `Manager` for multiplexing.
 * If the user summons:
 *
 *   `io('http://localhost/a');`
 *   `io('http://localhost/b');`
 *
 * We reuse the existing instance based on same scheme/port/host,
 * and we initialize sockets for each namespace.
 *
 * @api public
 */

function lookup (uri, opts) {
  if (typeof uri === 'object') {
    opts = uri;
    uri = undefined;
  }

  opts = opts || {};

  var parsed = url(uri);
  var source = parsed.source;
  var id = parsed.id;
  var path = parsed.path;
  var sameNamespace = cache[id] && path in cache[id].nsps;
  var newConnection = opts.forceNew || opts['force new connection'] ||
                      false === opts.multiplex || sameNamespace;

  var io;

  if (newConnection) {
    debug('ignoring socket cache for %s', source);
    io = Manager(source, opts);
  } else {
    if (!cache[id]) {
      debug('new io instance for %s', source);
      cache[id] = Manager(source, opts);
    }
    io = cache[id];
  }
  if (parsed.query && !opts.query) {
    opts.query = parsed.query;
  }
  return io.socket(parsed.path, opts);
}

/**
 * Protocol version.
 *
 * @api public
 */

exports.protocol = parser.protocol;

/**
 * `connect`.
 *
 * @param {String} uri
 * @api public
 */

exports.connect = lookup;

/**
 * Expose constructors for standalone build.
 *
 * @api public
 */

exports.Manager = __webpack_require__(/*! ./manager */ "./node_modules/socket.io-client/lib/manager.js");
exports.Socket = __webpack_require__(/*! ./socket */ "./node_modules/socket.io-client/lib/socket.js");


/***/ }),

/***/ "./node_modules/socket.io-client/lib/manager.js":
/*!******************************************************!*\
  !*** ./node_modules/socket.io-client/lib/manager.js ***!
  \******************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {


/**
 * Module dependencies.
 */

var eio = __webpack_require__(/*! engine.io-client */ "./node_modules/engine.io-client/lib/index.js");
var Socket = __webpack_require__(/*! ./socket */ "./node_modules/socket.io-client/lib/socket.js");
var Emitter = __webpack_require__(/*! component-emitter */ "./node_modules/component-emitter/index.js");
var parser = __webpack_require__(/*! socket.io-parser */ "./node_modules/socket.io-parser/index.js");
var on = __webpack_require__(/*! ./on */ "./node_modules/socket.io-client/lib/on.js");
var bind = __webpack_require__(/*! component-bind */ "./node_modules/component-bind/index.js");
var debug = __webpack_require__(/*! debug */ "./node_modules/debug/src/browser.js")('socket.io-client:manager');
var indexOf = __webpack_require__(/*! indexof */ "./node_modules/indexof/index.js");
var Backoff = __webpack_require__(/*! backo2 */ "./node_modules/backo2/index.js");

/**
 * IE6+ hasOwnProperty
 */

var has = Object.prototype.hasOwnProperty;

/**
 * Module exports
 */

module.exports = Manager;

/**
 * `Manager` constructor.
 *
 * @param {String} engine instance or engine uri/opts
 * @param {Object} options
 * @api public
 */

function Manager (uri, opts) {
  if (!(this instanceof Manager)) return new Manager(uri, opts);
  if (uri && ('object' === typeof uri)) {
    opts = uri;
    uri = undefined;
  }
  opts = opts || {};

  opts.path = opts.path || '/socket.io';
  this.nsps = {};
  this.subs = [];
  this.opts = opts;
  this.reconnection(opts.reconnection !== false);
  this.reconnectionAttempts(opts.reconnectionAttempts || Infinity);
  this.reconnectionDelay(opts.reconnectionDelay || 1000);
  this.reconnectionDelayMax(opts.reconnectionDelayMax || 5000);
  this.randomizationFactor(opts.randomizationFactor || 0.5);
  this.backoff = new Backoff({
    min: this.reconnectionDelay(),
    max: this.reconnectionDelayMax(),
    jitter: this.randomizationFactor()
  });
  this.timeout(null == opts.timeout ? 20000 : opts.timeout);
  this.readyState = 'closed';
  this.uri = uri;
  this.connecting = [];
  this.lastPing = null;
  this.encoding = false;
  this.packetBuffer = [];
  var _parser = opts.parser || parser;
  this.encoder = new _parser.Encoder();
  this.decoder = new _parser.Decoder();
  this.autoConnect = opts.autoConnect !== false;
  if (this.autoConnect) this.open();
}

/**
 * Propagate given event to sockets and emit on `this`
 *
 * @api private
 */

Manager.prototype.emitAll = function () {
  this.emit.apply(this, arguments);
  for (var nsp in this.nsps) {
    if (has.call(this.nsps, nsp)) {
      this.nsps[nsp].emit.apply(this.nsps[nsp], arguments);
    }
  }
};

/**
 * Update `socket.id` of all sockets
 *
 * @api private
 */

Manager.prototype.updateSocketIds = function () {
  for (var nsp in this.nsps) {
    if (has.call(this.nsps, nsp)) {
      this.nsps[nsp].id = this.generateId(nsp);
    }
  }
};

/**
 * generate `socket.id` for the given `nsp`
 *
 * @param {String} nsp
 * @return {String}
 * @api private
 */

Manager.prototype.generateId = function (nsp) {
  return (nsp === '/' ? '' : (nsp + '#')) + this.engine.id;
};

/**
 * Mix in `Emitter`.
 */

Emitter(Manager.prototype);

/**
 * Sets the `reconnection` config.
 *
 * @param {Boolean} true/false if it should automatically reconnect
 * @return {Manager} self or value
 * @api public
 */

Manager.prototype.reconnection = function (v) {
  if (!arguments.length) return this._reconnection;
  this._reconnection = !!v;
  return this;
};

/**
 * Sets the reconnection attempts config.
 *
 * @param {Number} max reconnection attempts before giving up
 * @return {Manager} self or value
 * @api public
 */

Manager.prototype.reconnectionAttempts = function (v) {
  if (!arguments.length) return this._reconnectionAttempts;
  this._reconnectionAttempts = v;
  return this;
};

/**
 * Sets the delay between reconnections.
 *
 * @param {Number} delay
 * @return {Manager} self or value
 * @api public
 */

Manager.prototype.reconnectionDelay = function (v) {
  if (!arguments.length) return this._reconnectionDelay;
  this._reconnectionDelay = v;
  this.backoff && this.backoff.setMin(v);
  return this;
};

Manager.prototype.randomizationFactor = function (v) {
  if (!arguments.length) return this._randomizationFactor;
  this._randomizationFactor = v;
  this.backoff && this.backoff.setJitter(v);
  return this;
};

/**
 * Sets the maximum delay between reconnections.
 *
 * @param {Number} delay
 * @return {Manager} self or value
 * @api public
 */

Manager.prototype.reconnectionDelayMax = function (v) {
  if (!arguments.length) return this._reconnectionDelayMax;
  this._reconnectionDelayMax = v;
  this.backoff && this.backoff.setMax(v);
  return this;
};

/**
 * Sets the connection timeout. `false` to disable
 *
 * @return {Manager} self or value
 * @api public
 */

Manager.prototype.timeout = function (v) {
  if (!arguments.length) return this._timeout;
  this._timeout = v;
  return this;
};

/**
 * Starts trying to reconnect if reconnection is enabled and we have not
 * started reconnecting yet
 *
 * @api private
 */

Manager.prototype.maybeReconnectOnOpen = function () {
  // Only try to reconnect if it's the first time we're connecting
  if (!this.reconnecting && this._reconnection && this.backoff.attempts === 0) {
    // keeps reconnection from firing twice for the same reconnection loop
    this.reconnect();
  }
};

/**
 * Sets the current transport `socket`.
 *
 * @param {Function} optional, callback
 * @return {Manager} self
 * @api public
 */

Manager.prototype.open =
Manager.prototype.connect = function (fn, opts) {
  debug('readyState %s', this.readyState);
  if (~this.readyState.indexOf('open')) return this;

  debug('opening %s', this.uri);
  this.engine = eio(this.uri, this.opts);
  var socket = this.engine;
  var self = this;
  this.readyState = 'opening';
  this.skipReconnect = false;

  // emit `open`
  var openSub = on(socket, 'open', function () {
    self.onopen();
    fn && fn();
  });

  // emit `connect_error`
  var errorSub = on(socket, 'error', function (data) {
    debug('connect_error');
    self.cleanup();
    self.readyState = 'closed';
    self.emitAll('connect_error', data);
    if (fn) {
      var err = new Error('Connection error');
      err.data = data;
      fn(err);
    } else {
      // Only do this if there is no fn to handle the error
      self.maybeReconnectOnOpen();
    }
  });

  // emit `connect_timeout`
  if (false !== this._timeout) {
    var timeout = this._timeout;
    debug('connect attempt will timeout after %d', timeout);

    // set timer
    var timer = setTimeout(function () {
      debug('connect attempt timed out after %d', timeout);
      openSub.destroy();
      socket.close();
      socket.emit('error', 'timeout');
      self.emitAll('connect_timeout', timeout);
    }, timeout);

    this.subs.push({
      destroy: function () {
        clearTimeout(timer);
      }
    });
  }

  this.subs.push(openSub);
  this.subs.push(errorSub);

  return this;
};

/**
 * Called upon transport open.
 *
 * @api private
 */

Manager.prototype.onopen = function () {
  debug('open');

  // clear old subs
  this.cleanup();

  // mark as open
  this.readyState = 'open';
  this.emit('open');

  // add new subs
  var socket = this.engine;
  this.subs.push(on(socket, 'data', bind(this, 'ondata')));
  this.subs.push(on(socket, 'ping', bind(this, 'onping')));
  this.subs.push(on(socket, 'pong', bind(this, 'onpong')));
  this.subs.push(on(socket, 'error', bind(this, 'onerror')));
  this.subs.push(on(socket, 'close', bind(this, 'onclose')));
  this.subs.push(on(this.decoder, 'decoded', bind(this, 'ondecoded')));
};

/**
 * Called upon a ping.
 *
 * @api private
 */

Manager.prototype.onping = function () {
  this.lastPing = new Date();
  this.emitAll('ping');
};

/**
 * Called upon a packet.
 *
 * @api private
 */

Manager.prototype.onpong = function () {
  this.emitAll('pong', new Date() - this.lastPing);
};

/**
 * Called with data.
 *
 * @api private
 */

Manager.prototype.ondata = function (data) {
  this.decoder.add(data);
};

/**
 * Called when parser fully decodes a packet.
 *
 * @api private
 */

Manager.prototype.ondecoded = function (packet) {
  this.emit('packet', packet);
};

/**
 * Called upon socket error.
 *
 * @api private
 */

Manager.prototype.onerror = function (err) {
  debug('error', err);
  this.emitAll('error', err);
};

/**
 * Creates a new socket for the given `nsp`.
 *
 * @return {Socket}
 * @api public
 */

Manager.prototype.socket = function (nsp, opts) {
  var socket = this.nsps[nsp];
  if (!socket) {
    socket = new Socket(this, nsp, opts);
    this.nsps[nsp] = socket;
    var self = this;
    socket.on('connecting', onConnecting);
    socket.on('connect', function () {
      socket.id = self.generateId(nsp);
    });

    if (this.autoConnect) {
      // manually call here since connecting event is fired before listening
      onConnecting();
    }
  }

  function onConnecting () {
    if (!~indexOf(self.connecting, socket)) {
      self.connecting.push(socket);
    }
  }

  return socket;
};

/**
 * Called upon a socket close.
 *
 * @param {Socket} socket
 */

Manager.prototype.destroy = function (socket) {
  var index = indexOf(this.connecting, socket);
  if (~index) this.connecting.splice(index, 1);
  if (this.connecting.length) return;

  this.close();
};

/**
 * Writes a packet.
 *
 * @param {Object} packet
 * @api private
 */

Manager.prototype.packet = function (packet) {
  debug('writing packet %j', packet);
  var self = this;
  if (packet.query && packet.type === 0) packet.nsp += '?' + packet.query;

  if (!self.encoding) {
    // encode, then write to engine with result
    self.encoding = true;
    this.encoder.encode(packet, function (encodedPackets) {
      for (var i = 0; i < encodedPackets.length; i++) {
        self.engine.write(encodedPackets[i], packet.options);
      }
      self.encoding = false;
      self.processPacketQueue();
    });
  } else { // add packet to the queue
    self.packetBuffer.push(packet);
  }
};

/**
 * If packet buffer is non-empty, begins encoding the
 * next packet in line.
 *
 * @api private
 */

Manager.prototype.processPacketQueue = function () {
  if (this.packetBuffer.length > 0 && !this.encoding) {
    var pack = this.packetBuffer.shift();
    this.packet(pack);
  }
};

/**
 * Clean up transport subscriptions and packet buffer.
 *
 * @api private
 */

Manager.prototype.cleanup = function () {
  debug('cleanup');

  var subsLength = this.subs.length;
  for (var i = 0; i < subsLength; i++) {
    var sub = this.subs.shift();
    sub.destroy();
  }

  this.packetBuffer = [];
  this.encoding = false;
  this.lastPing = null;

  this.decoder.destroy();
};

/**
 * Close the current socket.
 *
 * @api private
 */

Manager.prototype.close =
Manager.prototype.disconnect = function () {
  debug('disconnect');
  this.skipReconnect = true;
  this.reconnecting = false;
  if ('opening' === this.readyState) {
    // `onclose` will not fire because
    // an open event never happened
    this.cleanup();
  }
  this.backoff.reset();
  this.readyState = 'closed';
  if (this.engine) this.engine.close();
};

/**
 * Called upon engine close.
 *
 * @api private
 */

Manager.prototype.onclose = function (reason) {
  debug('onclose');

  this.cleanup();
  this.backoff.reset();
  this.readyState = 'closed';
  this.emit('close', reason);

  if (this._reconnection && !this.skipReconnect) {
    this.reconnect();
  }
};

/**
 * Attempt a reconnection.
 *
 * @api private
 */

Manager.prototype.reconnect = function () {
  if (this.reconnecting || this.skipReconnect) return this;

  var self = this;

  if (this.backoff.attempts >= this._reconnectionAttempts) {
    debug('reconnect failed');
    this.backoff.reset();
    this.emitAll('reconnect_failed');
    this.reconnecting = false;
  } else {
    var delay = this.backoff.duration();
    debug('will wait %dms before reconnect attempt', delay);

    this.reconnecting = true;
    var timer = setTimeout(function () {
      if (self.skipReconnect) return;

      debug('attempting reconnect');
      self.emitAll('reconnect_attempt', self.backoff.attempts);
      self.emitAll('reconnecting', self.backoff.attempts);

      // check again for the case socket closed in above events
      if (self.skipReconnect) return;

      self.open(function (err) {
        if (err) {
          debug('reconnect attempt error');
          self.reconnecting = false;
          self.reconnect();
          self.emitAll('reconnect_error', err.data);
        } else {
          debug('reconnect success');
          self.onreconnect();
        }
      });
    }, delay);

    this.subs.push({
      destroy: function () {
        clearTimeout(timer);
      }
    });
  }
};

/**
 * Called upon successful reconnect.
 *
 * @api private
 */

Manager.prototype.onreconnect = function () {
  var attempt = this.backoff.attempts;
  this.reconnecting = false;
  this.backoff.reset();
  this.updateSocketIds();
  this.emitAll('reconnect', attempt);
};


/***/ }),

/***/ "./node_modules/socket.io-client/lib/on.js":
/*!*************************************************!*\
  !*** ./node_modules/socket.io-client/lib/on.js ***!
  \*************************************************/
/*! no static exports found */
/***/ (function(module, exports) {


/**
 * Module exports.
 */

module.exports = on;

/**
 * Helper for subscriptions.
 *
 * @param {Object|EventEmitter} obj with `Emitter` mixin or `EventEmitter`
 * @param {String} event name
 * @param {Function} callback
 * @api public
 */

function on (obj, ev, fn) {
  obj.on(ev, fn);
  return {
    destroy: function () {
      obj.removeListener(ev, fn);
    }
  };
}


/***/ }),

/***/ "./node_modules/socket.io-client/lib/socket.js":
/*!*****************************************************!*\
  !*** ./node_modules/socket.io-client/lib/socket.js ***!
  \*****************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {


/**
 * Module dependencies.
 */

var parser = __webpack_require__(/*! socket.io-parser */ "./node_modules/socket.io-parser/index.js");
var Emitter = __webpack_require__(/*! component-emitter */ "./node_modules/component-emitter/index.js");
var toArray = __webpack_require__(/*! to-array */ "./node_modules/to-array/index.js");
var on = __webpack_require__(/*! ./on */ "./node_modules/socket.io-client/lib/on.js");
var bind = __webpack_require__(/*! component-bind */ "./node_modules/component-bind/index.js");
var debug = __webpack_require__(/*! debug */ "./node_modules/debug/src/browser.js")('socket.io-client:socket');
var parseqs = __webpack_require__(/*! parseqs */ "./node_modules/parseqs/index.js");
var hasBin = __webpack_require__(/*! has-binary2 */ "./node_modules/has-binary2/index.js");

/**
 * Module exports.
 */

module.exports = exports = Socket;

/**
 * Internal events (blacklisted).
 * These events can't be emitted by the user.
 *
 * @api private
 */

var events = {
  connect: 1,
  connect_error: 1,
  connect_timeout: 1,
  connecting: 1,
  disconnect: 1,
  error: 1,
  reconnect: 1,
  reconnect_attempt: 1,
  reconnect_failed: 1,
  reconnect_error: 1,
  reconnecting: 1,
  ping: 1,
  pong: 1
};

/**
 * Shortcut to `Emitter#emit`.
 */

var emit = Emitter.prototype.emit;

/**
 * `Socket` constructor.
 *
 * @api public
 */

function Socket (io, nsp, opts) {
  this.io = io;
  this.nsp = nsp;
  this.json = this; // compat
  this.ids = 0;
  this.acks = {};
  this.receiveBuffer = [];
  this.sendBuffer = [];
  this.connected = false;
  this.disconnected = true;
  this.flags = {};
  if (opts && opts.query) {
    this.query = opts.query;
  }
  if (this.io.autoConnect) this.open();
}

/**
 * Mix in `Emitter`.
 */

Emitter(Socket.prototype);

/**
 * Subscribe to open, close and packet events
 *
 * @api private
 */

Socket.prototype.subEvents = function () {
  if (this.subs) return;

  var io = this.io;
  this.subs = [
    on(io, 'open', bind(this, 'onopen')),
    on(io, 'packet', bind(this, 'onpacket')),
    on(io, 'close', bind(this, 'onclose'))
  ];
};

/**
 * "Opens" the socket.
 *
 * @api public
 */

Socket.prototype.open =
Socket.prototype.connect = function () {
  if (this.connected) return this;

  this.subEvents();
  this.io.open(); // ensure open
  if ('open' === this.io.readyState) this.onopen();
  this.emit('connecting');
  return this;
};

/**
 * Sends a `message` event.
 *
 * @return {Socket} self
 * @api public
 */

Socket.prototype.send = function () {
  var args = toArray(arguments);
  args.unshift('message');
  this.emit.apply(this, args);
  return this;
};

/**
 * Override `emit`.
 * If the event is in `events`, it's emitted normally.
 *
 * @param {String} event name
 * @return {Socket} self
 * @api public
 */

Socket.prototype.emit = function (ev) {
  if (events.hasOwnProperty(ev)) {
    emit.apply(this, arguments);
    return this;
  }

  var args = toArray(arguments);
  var packet = {
    type: (this.flags.binary !== undefined ? this.flags.binary : hasBin(args)) ? parser.BINARY_EVENT : parser.EVENT,
    data: args
  };

  packet.options = {};
  packet.options.compress = !this.flags || false !== this.flags.compress;

  // event ack callback
  if ('function' === typeof args[args.length - 1]) {
    debug('emitting packet with ack id %d', this.ids);
    this.acks[this.ids] = args.pop();
    packet.id = this.ids++;
  }

  if (this.connected) {
    this.packet(packet);
  } else {
    this.sendBuffer.push(packet);
  }

  this.flags = {};

  return this;
};

/**
 * Sends a packet.
 *
 * @param {Object} packet
 * @api private
 */

Socket.prototype.packet = function (packet) {
  packet.nsp = this.nsp;
  this.io.packet(packet);
};

/**
 * Called upon engine `open`.
 *
 * @api private
 */

Socket.prototype.onopen = function () {
  debug('transport is open - connecting');

  // write connect packet if necessary
  if ('/' !== this.nsp) {
    if (this.query) {
      var query = typeof this.query === 'object' ? parseqs.encode(this.query) : this.query;
      debug('sending connect packet with query %s', query);
      this.packet({type: parser.CONNECT, query: query});
    } else {
      this.packet({type: parser.CONNECT});
    }
  }
};

/**
 * Called upon engine `close`.
 *
 * @param {String} reason
 * @api private
 */

Socket.prototype.onclose = function (reason) {
  debug('close (%s)', reason);
  this.connected = false;
  this.disconnected = true;
  delete this.id;
  this.emit('disconnect', reason);
};

/**
 * Called with socket packet.
 *
 * @param {Object} packet
 * @api private
 */

Socket.prototype.onpacket = function (packet) {
  var sameNamespace = packet.nsp === this.nsp;
  var rootNamespaceError = packet.type === parser.ERROR && packet.nsp === '/';

  if (!sameNamespace && !rootNamespaceError) return;

  switch (packet.type) {
    case parser.CONNECT:
      this.onconnect();
      break;

    case parser.EVENT:
      this.onevent(packet);
      break;

    case parser.BINARY_EVENT:
      this.onevent(packet);
      break;

    case parser.ACK:
      this.onack(packet);
      break;

    case parser.BINARY_ACK:
      this.onack(packet);
      break;

    case parser.DISCONNECT:
      this.ondisconnect();
      break;

    case parser.ERROR:
      this.emit('error', packet.data);
      break;
  }
};

/**
 * Called upon a server event.
 *
 * @param {Object} packet
 * @api private
 */

Socket.prototype.onevent = function (packet) {
  var args = packet.data || [];
  debug('emitting event %j', args);

  if (null != packet.id) {
    debug('attaching ack callback to event');
    args.push(this.ack(packet.id));
  }

  if (this.connected) {
    emit.apply(this, args);
  } else {
    this.receiveBuffer.push(args);
  }
};

/**
 * Produces an ack callback to emit with an event.
 *
 * @api private
 */

Socket.prototype.ack = function (id) {
  var self = this;
  var sent = false;
  return function () {
    // prevent double callbacks
    if (sent) return;
    sent = true;
    var args = toArray(arguments);
    debug('sending ack %j', args);

    self.packet({
      type: hasBin(args) ? parser.BINARY_ACK : parser.ACK,
      id: id,
      data: args
    });
  };
};

/**
 * Called upon a server acknowlegement.
 *
 * @param {Object} packet
 * @api private
 */

Socket.prototype.onack = function (packet) {
  var ack = this.acks[packet.id];
  if ('function' === typeof ack) {
    debug('calling ack %s with %j', packet.id, packet.data);
    ack.apply(this, packet.data);
    delete this.acks[packet.id];
  } else {
    debug('bad ack %s', packet.id);
  }
};

/**
 * Called upon server connect.
 *
 * @api private
 */

Socket.prototype.onconnect = function () {
  this.connected = true;
  this.disconnected = false;
  this.emit('connect');
  this.emitBuffered();
};

/**
 * Emit buffered events (received and emitted).
 *
 * @api private
 */

Socket.prototype.emitBuffered = function () {
  var i;
  for (i = 0; i < this.receiveBuffer.length; i++) {
    emit.apply(this, this.receiveBuffer[i]);
  }
  this.receiveBuffer = [];

  for (i = 0; i < this.sendBuffer.length; i++) {
    this.packet(this.sendBuffer[i]);
  }
  this.sendBuffer = [];
};

/**
 * Called upon server disconnect.
 *
 * @api private
 */

Socket.prototype.ondisconnect = function () {
  debug('server disconnect (%s)', this.nsp);
  this.destroy();
  this.onclose('io server disconnect');
};

/**
 * Called upon forced client/server side disconnections,
 * this method ensures the manager stops tracking us and
 * that reconnections don't get triggered for this.
 *
 * @api private.
 */

Socket.prototype.destroy = function () {
  if (this.subs) {
    // clean subscriptions to avoid reconnections
    for (var i = 0; i < this.subs.length; i++) {
      this.subs[i].destroy();
    }
    this.subs = null;
  }

  this.io.destroy(this);
};

/**
 * Disconnects the socket manually.
 *
 * @return {Socket} self
 * @api public
 */

Socket.prototype.close =
Socket.prototype.disconnect = function () {
  if (this.connected) {
    debug('performing disconnect (%s)', this.nsp);
    this.packet({ type: parser.DISCONNECT });
  }

  // remove socket from pool
  this.destroy();

  if (this.connected) {
    // fire events
    this.onclose('io client disconnect');
  }
  return this;
};

/**
 * Sets the compress flag.
 *
 * @param {Boolean} if `true`, compresses the sending data
 * @return {Socket} self
 * @api public
 */

Socket.prototype.compress = function (compress) {
  this.flags.compress = compress;
  return this;
};

/**
 * Sets the binary flag
 *
 * @param {Boolean} whether the emitted data contains binary
 * @return {Socket} self
 * @api public
 */

Socket.prototype.binary = function (binary) {
  this.flags.binary = binary;
  return this;
};


/***/ }),

/***/ "./node_modules/socket.io-client/lib/url.js":
/*!**************************************************!*\
  !*** ./node_modules/socket.io-client/lib/url.js ***!
  \**************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {


/**
 * Module dependencies.
 */

var parseuri = __webpack_require__(/*! parseuri */ "./node_modules/parseuri/index.js");
var debug = __webpack_require__(/*! debug */ "./node_modules/debug/src/browser.js")('socket.io-client:url');

/**
 * Module exports.
 */

module.exports = url;

/**
 * URL parser.
 *
 * @param {String} url
 * @param {Object} An object meant to mimic window.location.
 *                 Defaults to window.location.
 * @api public
 */

function url (uri, loc) {
  var obj = uri;

  // default to window.location
  loc = loc || (typeof location !== 'undefined' && location);
  if (null == uri) uri = loc.protocol + '//' + loc.host;

  // relative path support
  if ('string' === typeof uri) {
    if ('/' === uri.charAt(0)) {
      if ('/' === uri.charAt(1)) {
        uri = loc.protocol + uri;
      } else {
        uri = loc.host + uri;
      }
    }

    if (!/^(https?|wss?):\/\//.test(uri)) {
      debug('protocol-less url %s', uri);
      if ('undefined' !== typeof loc) {
        uri = loc.protocol + '//' + uri;
      } else {
        uri = 'https://' + uri;
      }
    }

    // parse
    debug('parse %s', uri);
    obj = parseuri(uri);
  }

  // make sure we treat `localhost:80` and `localhost` equally
  if (!obj.port) {
    if (/^(http|ws)$/.test(obj.protocol)) {
      obj.port = '80';
    } else if (/^(http|ws)s$/.test(obj.protocol)) {
      obj.port = '443';
    }
  }

  obj.path = obj.path || '/';

  var ipv6 = obj.host.indexOf(':') !== -1;
  var host = ipv6 ? '[' + obj.host + ']' : obj.host;

  // define unique id
  obj.id = obj.protocol + '://' + host + ':' + obj.port;
  // define href
  obj.href = obj.protocol + '://' + host + (loc && loc.port === obj.port ? '' : (':' + obj.port));

  return obj;
}


/***/ }),

/***/ "./node_modules/socket.io-parser/binary.js":
/*!*************************************************!*\
  !*** ./node_modules/socket.io-parser/binary.js ***!
  \*************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

/*global Blob,File*/

/**
 * Module requirements
 */

var isArray = __webpack_require__(/*! isarray */ "./node_modules/isarray/index.js");
var isBuf = __webpack_require__(/*! ./is-buffer */ "./node_modules/socket.io-parser/is-buffer.js");
var toString = Object.prototype.toString;
var withNativeBlob = typeof Blob === 'function' || (typeof Blob !== 'undefined' && toString.call(Blob) === '[object BlobConstructor]');
var withNativeFile = typeof File === 'function' || (typeof File !== 'undefined' && toString.call(File) === '[object FileConstructor]');

/**
 * Replaces every Buffer | ArrayBuffer in packet with a numbered placeholder.
 * Anything with blobs or files should be fed through removeBlobs before coming
 * here.
 *
 * @param {Object} packet - socket.io event packet
 * @return {Object} with deconstructed packet and list of buffers
 * @api public
 */

exports.deconstructPacket = function(packet) {
  var buffers = [];
  var packetData = packet.data;
  var pack = packet;
  pack.data = _deconstructPacket(packetData, buffers);
  pack.attachments = buffers.length; // number of binary 'attachments'
  return {packet: pack, buffers: buffers};
};

function _deconstructPacket(data, buffers) {
  if (!data) return data;

  if (isBuf(data)) {
    var placeholder = { _placeholder: true, num: buffers.length };
    buffers.push(data);
    return placeholder;
  } else if (isArray(data)) {
    var newData = new Array(data.length);
    for (var i = 0; i < data.length; i++) {
      newData[i] = _deconstructPacket(data[i], buffers);
    }
    return newData;
  } else if (typeof data === 'object' && !(data instanceof Date)) {
    var newData = {};
    for (var key in data) {
      newData[key] = _deconstructPacket(data[key], buffers);
    }
    return newData;
  }
  return data;
}

/**
 * Reconstructs a binary packet from its placeholder packet and buffers
 *
 * @param {Object} packet - event packet with placeholders
 * @param {Array} buffers - binary buffers to put in placeholder positions
 * @return {Object} reconstructed packet
 * @api public
 */

exports.reconstructPacket = function(packet, buffers) {
  packet.data = _reconstructPacket(packet.data, buffers);
  packet.attachments = undefined; // no longer useful
  return packet;
};

function _reconstructPacket(data, buffers) {
  if (!data) return data;

  if (data && data._placeholder) {
    return buffers[data.num]; // appropriate buffer (should be natural order anyway)
  } else if (isArray(data)) {
    for (var i = 0; i < data.length; i++) {
      data[i] = _reconstructPacket(data[i], buffers);
    }
  } else if (typeof data === 'object') {
    for (var key in data) {
      data[key] = _reconstructPacket(data[key], buffers);
    }
  }

  return data;
}

/**
 * Asynchronously removes Blobs or Files from data via
 * FileReader's readAsArrayBuffer method. Used before encoding
 * data as msgpack. Calls callback with the blobless data.
 *
 * @param {Object} data
 * @param {Function} callback
 * @api private
 */

exports.removeBlobs = function(data, callback) {
  function _removeBlobs(obj, curKey, containingObject) {
    if (!obj) return obj;

    // convert any blob
    if ((withNativeBlob && obj instanceof Blob) ||
        (withNativeFile && obj instanceof File)) {
      pendingBlobs++;

      // async filereader
      var fileReader = new FileReader();
      fileReader.onload = function() { // this.result == arraybuffer
        if (containingObject) {
          containingObject[curKey] = this.result;
        }
        else {
          bloblessData = this.result;
        }

        // if nothing pending its callback time
        if(! --pendingBlobs) {
          callback(bloblessData);
        }
      };

      fileReader.readAsArrayBuffer(obj); // blob -> arraybuffer
    } else if (isArray(obj)) { // handle array
      for (var i = 0; i < obj.length; i++) {
        _removeBlobs(obj[i], i, obj);
      }
    } else if (typeof obj === 'object' && !isBuf(obj)) { // and object
      for (var key in obj) {
        _removeBlobs(obj[key], key, obj);
      }
    }
  }

  var pendingBlobs = 0;
  var bloblessData = data;
  _removeBlobs(bloblessData);
  if (!pendingBlobs) {
    callback(bloblessData);
  }
};


/***/ }),

/***/ "./node_modules/socket.io-parser/index.js":
/*!************************************************!*\
  !*** ./node_modules/socket.io-parser/index.js ***!
  \************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {


/**
 * Module dependencies.
 */

var debug = __webpack_require__(/*! debug */ "./node_modules/socket.io-parser/node_modules/debug/src/browser.js")('socket.io-parser');
var Emitter = __webpack_require__(/*! component-emitter */ "./node_modules/component-emitter/index.js");
var binary = __webpack_require__(/*! ./binary */ "./node_modules/socket.io-parser/binary.js");
var isArray = __webpack_require__(/*! isarray */ "./node_modules/isarray/index.js");
var isBuf = __webpack_require__(/*! ./is-buffer */ "./node_modules/socket.io-parser/is-buffer.js");

/**
 * Protocol version.
 *
 * @api public
 */

exports.protocol = 4;

/**
 * Packet types.
 *
 * @api public
 */

exports.types = [
  'CONNECT',
  'DISCONNECT',
  'EVENT',
  'ACK',
  'ERROR',
  'BINARY_EVENT',
  'BINARY_ACK'
];

/**
 * Packet type `connect`.
 *
 * @api public
 */

exports.CONNECT = 0;

/**
 * Packet type `disconnect`.
 *
 * @api public
 */

exports.DISCONNECT = 1;

/**
 * Packet type `event`.
 *
 * @api public
 */

exports.EVENT = 2;

/**
 * Packet type `ack`.
 *
 * @api public
 */

exports.ACK = 3;

/**
 * Packet type `error`.
 *
 * @api public
 */

exports.ERROR = 4;

/**
 * Packet type 'binary event'
 *
 * @api public
 */

exports.BINARY_EVENT = 5;

/**
 * Packet type `binary ack`. For acks with binary arguments.
 *
 * @api public
 */

exports.BINARY_ACK = 6;

/**
 * Encoder constructor.
 *
 * @api public
 */

exports.Encoder = Encoder;

/**
 * Decoder constructor.
 *
 * @api public
 */

exports.Decoder = Decoder;

/**
 * A socket.io Encoder instance
 *
 * @api public
 */

function Encoder() {}

var ERROR_PACKET = exports.ERROR + '"encode error"';

/**
 * Encode a packet as a single string if non-binary, or as a
 * buffer sequence, depending on packet type.
 *
 * @param {Object} obj - packet object
 * @param {Function} callback - function to handle encodings (likely engine.write)
 * @return Calls callback with Array of encodings
 * @api public
 */

Encoder.prototype.encode = function(obj, callback){
  debug('encoding packet %j', obj);

  if (exports.BINARY_EVENT === obj.type || exports.BINARY_ACK === obj.type) {
    encodeAsBinary(obj, callback);
  } else {
    var encoding = encodeAsString(obj);
    callback([encoding]);
  }
};

/**
 * Encode packet as string.
 *
 * @param {Object} packet
 * @return {String} encoded
 * @api private
 */

function encodeAsString(obj) {

  // first is type
  var str = '' + obj.type;

  // attachments if we have them
  if (exports.BINARY_EVENT === obj.type || exports.BINARY_ACK === obj.type) {
    str += obj.attachments + '-';
  }

  // if we have a namespace other than `/`
  // we append it followed by a comma `,`
  if (obj.nsp && '/' !== obj.nsp) {
    str += obj.nsp + ',';
  }

  // immediately followed by the id
  if (null != obj.id) {
    str += obj.id;
  }

  // json data
  if (null != obj.data) {
    var payload = tryStringify(obj.data);
    if (payload !== false) {
      str += payload;
    } else {
      return ERROR_PACKET;
    }
  }

  debug('encoded %j as %s', obj, str);
  return str;
}

function tryStringify(str) {
  try {
    return JSON.stringify(str);
  } catch(e){
    return false;
  }
}

/**
 * Encode packet as 'buffer sequence' by removing blobs, and
 * deconstructing packet into object with placeholders and
 * a list of buffers.
 *
 * @param {Object} packet
 * @return {Buffer} encoded
 * @api private
 */

function encodeAsBinary(obj, callback) {

  function writeEncoding(bloblessData) {
    var deconstruction = binary.deconstructPacket(bloblessData);
    var pack = encodeAsString(deconstruction.packet);
    var buffers = deconstruction.buffers;

    buffers.unshift(pack); // add packet info to beginning of data list
    callback(buffers); // write all the buffers
  }

  binary.removeBlobs(obj, writeEncoding);
}

/**
 * A socket.io Decoder instance
 *
 * @return {Object} decoder
 * @api public
 */

function Decoder() {
  this.reconstructor = null;
}

/**
 * Mix in `Emitter` with Decoder.
 */

Emitter(Decoder.prototype);

/**
 * Decodes an encoded packet string into packet JSON.
 *
 * @param {String} obj - encoded packet
 * @return {Object} packet
 * @api public
 */

Decoder.prototype.add = function(obj) {
  var packet;
  if (typeof obj === 'string') {
    packet = decodeString(obj);
    if (exports.BINARY_EVENT === packet.type || exports.BINARY_ACK === packet.type) { // binary packet's json
      this.reconstructor = new BinaryReconstructor(packet);

      // no attachments, labeled binary but no binary data to follow
      if (this.reconstructor.reconPack.attachments === 0) {
        this.emit('decoded', packet);
      }
    } else { // non-binary full packet
      this.emit('decoded', packet);
    }
  } else if (isBuf(obj) || obj.base64) { // raw binary data
    if (!this.reconstructor) {
      throw new Error('got binary data when not reconstructing a packet');
    } else {
      packet = this.reconstructor.takeBinaryData(obj);
      if (packet) { // received final buffer
        this.reconstructor = null;
        this.emit('decoded', packet);
      }
    }
  } else {
    throw new Error('Unknown type: ' + obj);
  }
};

/**
 * Decode a packet String (JSON data)
 *
 * @param {String} str
 * @return {Object} packet
 * @api private
 */

function decodeString(str) {
  var i = 0;
  // look up type
  var p = {
    type: Number(str.charAt(0))
  };

  if (null == exports.types[p.type]) {
    return error('unknown packet type ' + p.type);
  }

  // look up attachments if type binary
  if (exports.BINARY_EVENT === p.type || exports.BINARY_ACK === p.type) {
    var buf = '';
    while (str.charAt(++i) !== '-') {
      buf += str.charAt(i);
      if (i == str.length) break;
    }
    if (buf != Number(buf) || str.charAt(i) !== '-') {
      throw new Error('Illegal attachments');
    }
    p.attachments = Number(buf);
  }

  // look up namespace (if any)
  if ('/' === str.charAt(i + 1)) {
    p.nsp = '';
    while (++i) {
      var c = str.charAt(i);
      if (',' === c) break;
      p.nsp += c;
      if (i === str.length) break;
    }
  } else {
    p.nsp = '/';
  }

  // look up id
  var next = str.charAt(i + 1);
  if ('' !== next && Number(next) == next) {
    p.id = '';
    while (++i) {
      var c = str.charAt(i);
      if (null == c || Number(c) != c) {
        --i;
        break;
      }
      p.id += str.charAt(i);
      if (i === str.length) break;
    }
    p.id = Number(p.id);
  }

  // look up json data
  if (str.charAt(++i)) {
    var payload = tryParse(str.substr(i));
    var isPayloadValid = payload !== false && (p.type === exports.ERROR || isArray(payload));
    if (isPayloadValid) {
      p.data = payload;
    } else {
      return error('invalid payload');
    }
  }

  debug('decoded %s as %j', str, p);
  return p;
}

function tryParse(str) {
  try {
    return JSON.parse(str);
  } catch(e){
    return false;
  }
}

/**
 * Deallocates a parser's resources
 *
 * @api public
 */

Decoder.prototype.destroy = function() {
  if (this.reconstructor) {
    this.reconstructor.finishedReconstruction();
  }
};

/**
 * A manager of a binary event's 'buffer sequence'. Should
 * be constructed whenever a packet of type BINARY_EVENT is
 * decoded.
 *
 * @param {Object} packet
 * @return {BinaryReconstructor} initialized reconstructor
 * @api private
 */

function BinaryReconstructor(packet) {
  this.reconPack = packet;
  this.buffers = [];
}

/**
 * Method to be called when binary data received from connection
 * after a BINARY_EVENT packet.
 *
 * @param {Buffer | ArrayBuffer} binData - the raw binary data received
 * @return {null | Object} returns null if more binary data is expected or
 *   a reconstructed packet object if all buffers have been received.
 * @api private
 */

BinaryReconstructor.prototype.takeBinaryData = function(binData) {
  this.buffers.push(binData);
  if (this.buffers.length === this.reconPack.attachments) { // done with buffer list
    var packet = binary.reconstructPacket(this.reconPack, this.buffers);
    this.finishedReconstruction();
    return packet;
  }
  return null;
};

/**
 * Cleans up binary packet reconstruction variables.
 *
 * @api private
 */

BinaryReconstructor.prototype.finishedReconstruction = function() {
  this.reconPack = null;
  this.buffers = [];
};

function error(msg) {
  return {
    type: exports.ERROR,
    data: 'parser error: ' + msg
  };
}


/***/ }),

/***/ "./node_modules/socket.io-parser/is-buffer.js":
/*!****************************************************!*\
  !*** ./node_modules/socket.io-parser/is-buffer.js ***!
  \****************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

/* WEBPACK VAR INJECTION */(function(Buffer) {
module.exports = isBuf;

var withNativeBuffer = typeof Buffer === 'function' && typeof Buffer.isBuffer === 'function';
var withNativeArrayBuffer = typeof ArrayBuffer === 'function';

var isView = function (obj) {
  return typeof ArrayBuffer.isView === 'function' ? ArrayBuffer.isView(obj) : (obj.buffer instanceof ArrayBuffer);
};

/**
 * Returns true if obj is a buffer or an arraybuffer.
 *
 * @api private
 */

function isBuf(obj) {
  return (withNativeBuffer && Buffer.isBuffer(obj)) ||
          (withNativeArrayBuffer && (obj instanceof ArrayBuffer || isView(obj)));
}

/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./../buffer/index.js */ "./node_modules/buffer/index.js").Buffer))

/***/ }),

/***/ "./node_modules/socket.io-parser/node_modules/debug/src/browser.js":
/*!*************************************************************************!*\
  !*** ./node_modules/socket.io-parser/node_modules/debug/src/browser.js ***!
  \*************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

/* WEBPACK VAR INJECTION */(function(process) {/**
 * This is the web browser implementation of `debug()`.
 *
 * Expose `debug()` as the module.
 */

exports = module.exports = __webpack_require__(/*! ./debug */ "./node_modules/socket.io-parser/node_modules/debug/src/debug.js");
exports.log = log;
exports.formatArgs = formatArgs;
exports.save = save;
exports.load = load;
exports.useColors = useColors;
exports.storage = 'undefined' != typeof chrome
               && 'undefined' != typeof chrome.storage
                  ? chrome.storage.local
                  : localstorage();

/**
 * Colors.
 */

exports.colors = [
  '#0000CC', '#0000FF', '#0033CC', '#0033FF', '#0066CC', '#0066FF', '#0099CC',
  '#0099FF', '#00CC00', '#00CC33', '#00CC66', '#00CC99', '#00CCCC', '#00CCFF',
  '#3300CC', '#3300FF', '#3333CC', '#3333FF', '#3366CC', '#3366FF', '#3399CC',
  '#3399FF', '#33CC00', '#33CC33', '#33CC66', '#33CC99', '#33CCCC', '#33CCFF',
  '#6600CC', '#6600FF', '#6633CC', '#6633FF', '#66CC00', '#66CC33', '#9900CC',
  '#9900FF', '#9933CC', '#9933FF', '#99CC00', '#99CC33', '#CC0000', '#CC0033',
  '#CC0066', '#CC0099', '#CC00CC', '#CC00FF', '#CC3300', '#CC3333', '#CC3366',
  '#CC3399', '#CC33CC', '#CC33FF', '#CC6600', '#CC6633', '#CC9900', '#CC9933',
  '#CCCC00', '#CCCC33', '#FF0000', '#FF0033', '#FF0066', '#FF0099', '#FF00CC',
  '#FF00FF', '#FF3300', '#FF3333', '#FF3366', '#FF3399', '#FF33CC', '#FF33FF',
  '#FF6600', '#FF6633', '#FF9900', '#FF9933', '#FFCC00', '#FFCC33'
];

/**
 * Currently only WebKit-based Web Inspectors, Firefox >= v31,
 * and the Firebug extension (any Firefox version) are known
 * to support "%c" CSS customizations.
 *
 * TODO: add a `localStorage` variable to explicitly enable/disable colors
 */

function useColors() {
  // NB: In an Electron preload script, document will be defined but not fully
  // initialized. Since we know we're in Chrome, we'll just detect this case
  // explicitly
  if (typeof window !== 'undefined' && window.process && window.process.type === 'renderer') {
    return true;
  }

  // Internet Explorer and Edge do not support colors.
  if (typeof navigator !== 'undefined' && navigator.userAgent && navigator.userAgent.toLowerCase().match(/(edge|trident)\/(\d+)/)) {
    return false;
  }

  // is webkit? http://stackoverflow.com/a/16459606/376773
  // document is undefined in react-native: https://github.com/facebook/react-native/pull/1632
  return (typeof document !== 'undefined' && document.documentElement && document.documentElement.style && document.documentElement.style.WebkitAppearance) ||
    // is firebug? http://stackoverflow.com/a/398120/376773
    (typeof window !== 'undefined' && window.console && (window.console.firebug || (window.console.exception && window.console.table))) ||
    // is firefox >= v31?
    // https://developer.mozilla.org/en-US/docs/Tools/Web_Console#Styling_messages
    (typeof navigator !== 'undefined' && navigator.userAgent && navigator.userAgent.toLowerCase().match(/firefox\/(\d+)/) && parseInt(RegExp.$1, 10) >= 31) ||
    // double check webkit in userAgent just in case we are in a worker
    (typeof navigator !== 'undefined' && navigator.userAgent && navigator.userAgent.toLowerCase().match(/applewebkit\/(\d+)/));
}

/**
 * Map %j to `JSON.stringify()`, since no Web Inspectors do that by default.
 */

exports.formatters.j = function(v) {
  try {
    return JSON.stringify(v);
  } catch (err) {
    return '[UnexpectedJSONParseError]: ' + err.message;
  }
};


/**
 * Colorize log arguments if enabled.
 *
 * @api public
 */

function formatArgs(args) {
  var useColors = this.useColors;

  args[0] = (useColors ? '%c' : '')
    + this.namespace
    + (useColors ? ' %c' : ' ')
    + args[0]
    + (useColors ? '%c ' : ' ')
    + '+' + exports.humanize(this.diff);

  if (!useColors) return;

  var c = 'color: ' + this.color;
  args.splice(1, 0, c, 'color: inherit')

  // the final "%c" is somewhat tricky, because there could be other
  // arguments passed either before or after the %c, so we need to
  // figure out the correct index to insert the CSS into
  var index = 0;
  var lastC = 0;
  args[0].replace(/%[a-zA-Z%]/g, function(match) {
    if ('%%' === match) return;
    index++;
    if ('%c' === match) {
      // we only are interested in the *last* %c
      // (the user may have provided their own)
      lastC = index;
    }
  });

  args.splice(lastC, 0, c);
}

/**
 * Invokes `console.log()` when available.
 * No-op when `console.log` is not a "function".
 *
 * @api public
 */

function log() {
  // this hackery is required for IE8/9, where
  // the `console.log` function doesn't have 'apply'
  return 'object' === typeof console
    && console.log
    && Function.prototype.apply.call(console.log, console, arguments);
}

/**
 * Save `namespaces`.
 *
 * @param {String} namespaces
 * @api private
 */

function save(namespaces) {
  try {
    if (null == namespaces) {
      exports.storage.removeItem('debug');
    } else {
      exports.storage.debug = namespaces;
    }
  } catch(e) {}
}

/**
 * Load `namespaces`.
 *
 * @return {String} returns the previously persisted debug modes
 * @api private
 */

function load() {
  var r;
  try {
    r = exports.storage.debug;
  } catch(e) {}

  // If debug isn't set in LS, and we're in Electron, try to load $DEBUG
  if (!r && typeof process !== 'undefined' && 'env' in process) {
    r = process.env.DEBUG;
  }

  return r;
}

/**
 * Enable namespaces listed in `localStorage.debug` initially.
 */

exports.enable(load());

/**
 * Localstorage attempts to return the localstorage.
 *
 * This is necessary because safari throws
 * when a user disables cookies/localstorage
 * and you attempt to access it.
 *
 * @return {LocalStorage}
 * @api private
 */

function localstorage() {
  try {
    return window.localStorage;
  } catch (e) {}
}

/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./../../../../process/browser.js */ "./node_modules/process/browser.js")))

/***/ }),

/***/ "./node_modules/socket.io-parser/node_modules/debug/src/debug.js":
/*!***********************************************************************!*\
  !*** ./node_modules/socket.io-parser/node_modules/debug/src/debug.js ***!
  \***********************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {


/**
 * This is the common logic for both the Node.js and web browser
 * implementations of `debug()`.
 *
 * Expose `debug()` as the module.
 */

exports = module.exports = createDebug.debug = createDebug['default'] = createDebug;
exports.coerce = coerce;
exports.disable = disable;
exports.enable = enable;
exports.enabled = enabled;
exports.humanize = __webpack_require__(/*! ms */ "./node_modules/socket.io-parser/node_modules/ms/index.js");

/**
 * Active `debug` instances.
 */
exports.instances = [];

/**
 * The currently active debug mode names, and names to skip.
 */

exports.names = [];
exports.skips = [];

/**
 * Map of special "%n" handling functions, for the debug "format" argument.
 *
 * Valid key names are a single, lower or upper-case letter, i.e. "n" and "N".
 */

exports.formatters = {};

/**
 * Select a color.
 * @param {String} namespace
 * @return {Number}
 * @api private
 */

function selectColor(namespace) {
  var hash = 0, i;

  for (i in namespace) {
    hash  = ((hash << 5) - hash) + namespace.charCodeAt(i);
    hash |= 0; // Convert to 32bit integer
  }

  return exports.colors[Math.abs(hash) % exports.colors.length];
}

/**
 * Create a debugger with the given `namespace`.
 *
 * @param {String} namespace
 * @return {Function}
 * @api public
 */

function createDebug(namespace) {

  var prevTime;

  function debug() {
    // disabled?
    if (!debug.enabled) return;

    var self = debug;

    // set `diff` timestamp
    var curr = +new Date();
    var ms = curr - (prevTime || curr);
    self.diff = ms;
    self.prev = prevTime;
    self.curr = curr;
    prevTime = curr;

    // turn the `arguments` into a proper Array
    var args = new Array(arguments.length);
    for (var i = 0; i < args.length; i++) {
      args[i] = arguments[i];
    }

    args[0] = exports.coerce(args[0]);

    if ('string' !== typeof args[0]) {
      // anything else let's inspect with %O
      args.unshift('%O');
    }

    // apply any `formatters` transformations
    var index = 0;
    args[0] = args[0].replace(/%([a-zA-Z%])/g, function(match, format) {
      // if we encounter an escaped % then don't increase the array index
      if (match === '%%') return match;
      index++;
      var formatter = exports.formatters[format];
      if ('function' === typeof formatter) {
        var val = args[index];
        match = formatter.call(self, val);

        // now we need to remove `args[index]` since it's inlined in the `format`
        args.splice(index, 1);
        index--;
      }
      return match;
    });

    // apply env-specific formatting (colors, etc.)
    exports.formatArgs.call(self, args);

    var logFn = debug.log || exports.log || console.log.bind(console);
    logFn.apply(self, args);
  }

  debug.namespace = namespace;
  debug.enabled = exports.enabled(namespace);
  debug.useColors = exports.useColors();
  debug.color = selectColor(namespace);
  debug.destroy = destroy;

  // env-specific initialization logic for debug instances
  if ('function' === typeof exports.init) {
    exports.init(debug);
  }

  exports.instances.push(debug);

  return debug;
}

function destroy () {
  var index = exports.instances.indexOf(this);
  if (index !== -1) {
    exports.instances.splice(index, 1);
    return true;
  } else {
    return false;
  }
}

/**
 * Enables a debug mode by namespaces. This can include modes
 * separated by a colon and wildcards.
 *
 * @param {String} namespaces
 * @api public
 */

function enable(namespaces) {
  exports.save(namespaces);

  exports.names = [];
  exports.skips = [];

  var i;
  var split = (typeof namespaces === 'string' ? namespaces : '').split(/[\s,]+/);
  var len = split.length;

  for (i = 0; i < len; i++) {
    if (!split[i]) continue; // ignore empty strings
    namespaces = split[i].replace(/\*/g, '.*?');
    if (namespaces[0] === '-') {
      exports.skips.push(new RegExp('^' + namespaces.substr(1) + '$'));
    } else {
      exports.names.push(new RegExp('^' + namespaces + '$'));
    }
  }

  for (i = 0; i < exports.instances.length; i++) {
    var instance = exports.instances[i];
    instance.enabled = exports.enabled(instance.namespace);
  }
}

/**
 * Disable debug output.
 *
 * @api public
 */

function disable() {
  exports.enable('');
}

/**
 * Returns true if the given mode name is enabled, false otherwise.
 *
 * @param {String} name
 * @return {Boolean}
 * @api public
 */

function enabled(name) {
  if (name[name.length - 1] === '*') {
    return true;
  }
  var i, len;
  for (i = 0, len = exports.skips.length; i < len; i++) {
    if (exports.skips[i].test(name)) {
      return false;
    }
  }
  for (i = 0, len = exports.names.length; i < len; i++) {
    if (exports.names[i].test(name)) {
      return true;
    }
  }
  return false;
}

/**
 * Coerce `val`.
 *
 * @param {Mixed} val
 * @return {Mixed}
 * @api private
 */

function coerce(val) {
  if (val instanceof Error) return val.stack || val.message;
  return val;
}


/***/ }),

/***/ "./node_modules/socket.io-parser/node_modules/ms/index.js":
/*!****************************************************************!*\
  !*** ./node_modules/socket.io-parser/node_modules/ms/index.js ***!
  \****************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

/**
 * Helpers.
 */

var s = 1000;
var m = s * 60;
var h = m * 60;
var d = h * 24;
var y = d * 365.25;

/**
 * Parse or format the given `val`.
 *
 * Options:
 *
 *  - `long` verbose formatting [false]
 *
 * @param {String|Number} val
 * @param {Object} [options]
 * @throws {Error} throw an error if val is not a non-empty string or a number
 * @return {String|Number}
 * @api public
 */

module.exports = function(val, options) {
  options = options || {};
  var type = typeof val;
  if (type === 'string' && val.length > 0) {
    return parse(val);
  } else if (type === 'number' && isNaN(val) === false) {
    return options.long ? fmtLong(val) : fmtShort(val);
  }
  throw new Error(
    'val is not a non-empty string or a valid number. val=' +
      JSON.stringify(val)
  );
};

/**
 * Parse the given `str` and return milliseconds.
 *
 * @param {String} str
 * @return {Number}
 * @api private
 */

function parse(str) {
  str = String(str);
  if (str.length > 100) {
    return;
  }
  var match = /^((?:\d+)?\.?\d+) *(milliseconds?|msecs?|ms|seconds?|secs?|s|minutes?|mins?|m|hours?|hrs?|h|days?|d|years?|yrs?|y)?$/i.exec(
    str
  );
  if (!match) {
    return;
  }
  var n = parseFloat(match[1]);
  var type = (match[2] || 'ms').toLowerCase();
  switch (type) {
    case 'years':
    case 'year':
    case 'yrs':
    case 'yr':
    case 'y':
      return n * y;
    case 'days':
    case 'day':
    case 'd':
      return n * d;
    case 'hours':
    case 'hour':
    case 'hrs':
    case 'hr':
    case 'h':
      return n * h;
    case 'minutes':
    case 'minute':
    case 'mins':
    case 'min':
    case 'm':
      return n * m;
    case 'seconds':
    case 'second':
    case 'secs':
    case 'sec':
    case 's':
      return n * s;
    case 'milliseconds':
    case 'millisecond':
    case 'msecs':
    case 'msec':
    case 'ms':
      return n;
    default:
      return undefined;
  }
}

/**
 * Short format for `ms`.
 *
 * @param {Number} ms
 * @return {String}
 * @api private
 */

function fmtShort(ms) {
  if (ms >= d) {
    return Math.round(ms / d) + 'd';
  }
  if (ms >= h) {
    return Math.round(ms / h) + 'h';
  }
  if (ms >= m) {
    return Math.round(ms / m) + 'm';
  }
  if (ms >= s) {
    return Math.round(ms / s) + 's';
  }
  return ms + 'ms';
}

/**
 * Long format for `ms`.
 *
 * @param {Number} ms
 * @return {String}
 * @api private
 */

function fmtLong(ms) {
  return plural(ms, d, 'day') ||
    plural(ms, h, 'hour') ||
    plural(ms, m, 'minute') ||
    plural(ms, s, 'second') ||
    ms + ' ms';
}

/**
 * Pluralization helper.
 */

function plural(ms, n, name) {
  if (ms < n) {
    return;
  }
  if (ms < n * 1.5) {
    return Math.floor(ms / n) + ' ' + name;
  }
  return Math.ceil(ms / n) + ' ' + name + 's';
}


/***/ }),

/***/ "./node_modules/to-array/index.js":
/*!****************************************!*\
  !*** ./node_modules/to-array/index.js ***!
  \****************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = toArray

function toArray(list, index) {
    var array = []

    index = index || 0

    for (var i = index || 0; i < list.length; i++) {
        array[i - index] = list[i]
    }

    return array
}


/***/ }),

/***/ "./node_modules/webpack/buildin/amd-define.js":
/*!***************************************!*\
  !*** (webpack)/buildin/amd-define.js ***!
  \***************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = function() {
	throw new Error("define cannot be used indirect");
};


/***/ }),

/***/ "./node_modules/webpack/buildin/global.js":
/*!***********************************!*\
  !*** (webpack)/buildin/global.js ***!
  \***********************************/
/*! no static exports found */
/***/ (function(module, exports) {

var g;

// This works in non-strict mode
g = (function() {
	return this;
})();

try {
	// This works if eval is allowed (see CSP)
	g = g || new Function("return this")();
} catch (e) {
	// This works if the window reference is available
	if (typeof window === "object") g = window;
}

// g can still be undefined, but nothing to do about it...
// We return undefined, instead of nothing here, so it's
// easier to handle this case. if(!global) { ...}

module.exports = g;


/***/ }),

/***/ "./node_modules/yeast/index.js":
/*!*************************************!*\
  !*** ./node_modules/yeast/index.js ***!
  \*************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var alphabet = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_'.split('')
  , length = 64
  , map = {}
  , seed = 0
  , i = 0
  , prev;

/**
 * Return a string representing the specified number.
 *
 * @param {Number} num The number to convert.
 * @returns {String} The string representation of the number.
 * @api public
 */
function encode(num) {
  var encoded = '';

  do {
    encoded = alphabet[num % length] + encoded;
    num = Math.floor(num / length);
  } while (num > 0);

  return encoded;
}

/**
 * Return the integer value specified by the given string.
 *
 * @param {String} str The string to convert.
 * @returns {Number} The integer value represented by the string.
 * @api public
 */
function decode(str) {
  var decoded = 0;

  for (i = 0; i < str.length; i++) {
    decoded = decoded * length + map[str.charAt(i)];
  }

  return decoded;
}

/**
 * Yeast: A tiny growing id generator.
 *
 * @returns {String} A unique id.
 * @api public
 */
function yeast() {
  var now = encode(+new Date());

  if (now !== prev) return seed = 0, prev = now;
  return now +'.'+ encode(seed++);
}

//
// Map each character to its index.
//
for (; i < length; i++) map[alphabet[i]] = i;

//
// Expose the `yeast`, `encode` and `decode` functions.
//
yeast.encode = encode;
yeast.decode = decode;
module.exports = yeast;


/***/ }),

/***/ "./src/conference.ts":
/*!***************************!*\
  !*** ./src/conference.ts ***!
  \***************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * -
 *  * Copyright (C) 2019 TeamApps.org
 *  * ---
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
var socket_io_client_1 = __importDefault(__webpack_require__(/*! socket.io-client */ "./node_modules/socket.io-client/lib/index.js"));
var mediasoupClient = __importStar(__webpack_require__(/*! mediasoup-client */ "./node_modules/mediasoup-client/lib-es5/index.js"));
var Conference = /** @class */ (function () {
    function Conference(data) {
        this.videoContainer = null;
        this.kind = '';
        this.streamActiveTimeout = [];
        this._sendStream = new MediaStream();
        this._adjustProfile = function () { };
        this.ws = '';
        this.room = '';
        this.peers = '';
        this.transport = {};
        this.producers = {};
        this.lastProduced = {};
        this.server_url = (data.params && data.params.serverUrl) || (window.location.protocol + '//' + window.location.hostname + (window.location.port ? (':' + window.location.port) : ''));
        this.uid = data.uid;
        this.token = data.token;
        this.params = __assign({ minBitrate: null, maxBitrate: null, localVideo: '#video', qualityChangerSelector: '', constraints: {
                audio: true,
                video: true
            }, errorAutoPlayCallback: function (video, error) {
                console.error('Playback error');
                console.error(video, error);
            }, onProfileChange: function () {
            } }, data.params);
        this.ms = mediasoupClient;
    }
    Conference.prototype.getPermissionsUrl = function (kind) {
        var tokenPart = kind === 'publish' ? '/' + this.token : '';
        this.kind = kind;
        return this.server_url + '/' + this.kind + '/' + this.uid + (this.token ? tokenPart : '');
    };
    ;
    Conference.prototype.request = function (url, method, body) {
        return new Promise(function (resolve, reject) {
            var xhr = new XMLHttpRequest();
            xhr.responseType = 'json';
            xhr.open(method, url);
            xhr.onload = function () {
                if (xhr.status >= 200 && xhr.status < 300) {
                    resolve(xhr.response);
                }
                else {
                    reject(xhr);
                }
            };
            xhr.onerror = function () { return reject(xhr); };
            xhr.send(body);
        });
    };
    ;
    Conference.prototype.captureDevice = function () {
        var _this = this;
        return new Promise(function (resolve, reject) {
            window.navigator.mediaDevices.getUserMedia(_this.params.constraints)
                .then(function (stream) {
                _this.__setVideoSource(_this.videoContainer);
                var capturing = {
                    stream: stream,
                    audio: _this.params.constraints.audio,
                    video: _this.params.constraints.video,
                };
                _this.__hookup(capturing);
                resolve('Device captured');
            })
                .catch(function (error) {
                reject(error);
            });
        });
    };
    ;
    Conference.prototype.setupRoom = function (data) {
        var _this = this;
        return new Promise(function (resolve, reject) {
            if (!_this.ms.isDeviceSupported()) {
                reject(new Error('Sorry, WebRTC is not supported on this device'));
            }
            var room;
            var maxBitrate = _this.params.maxBitrate || null;
            var channel = _this.uid;
            var kind = _this.kind === 'playback' ? 'subscribe' : 'publish';
            var peerName = (kind === 'publish') ? 'publish' : '' + Math.random();
            var socket = socket_io_client_1.default(data.server.url, { secure: true });
            socket.on('connect', function () {
                var socketEmit = function (action, data, callback, errback) {
                    socket.emit(action, { data: data, channel: channel, kind: kind, maxBitrate: maxBitrate }, function (response) {
                        if (response.errorId || response.error) {
                            if (errback) {
                                errback();
                            }
                        }
                        else {
                            if (callback) {
                                callback(response);
                            }
                        }
                    });
                };
                var turnServers;
                if (window.navigator && window.navigator.userAgent.match(/\sEdge\//)) {
                    turnServers = data.iceServers.map(function (srv) {
                        var urls = srv.urls.filter(function (url) {
                            return !url.match(/^turns:/);
                        });
                        return Object.assign({}, srv, { urls: urls });
                    });
                }
                room = new _this.ms.Room({
                    requestTimeout: 8000,
                    turnServers: turnServers || data.iceServers,
                });
                room.on('request', function (request, callback, errback) {
                    socketEmit('MS_SEND', request, callback, errback);
                });
                room.on('notify', function (notification) {
                    socketEmit('MS_NOTIFY', notification, function () { }, function () { });
                });
                room.join(peerName)
                    .then(function (peers) {
                    console.log('Channel', channel, 'joined with peers', peers);
                    var obj = { ws: socket, room: room, peers: peers };
                    resolve(obj);
                })
                    .catch(reject);
            });
            socket.on('disconnect', function (error) {
                console.log('disconnect', error);
            });
            socket.on('MS_NOTIFY', function (response) {
                room.receiveNotification(response);
            });
        });
    };
    ;
    Conference.prototype.startSendStream = function () {
        var _this = this;
        if (!this._sendStream) {
            throw new Error('No sending stream yet');
        }
        console.log('Streaming');
        this.__whenStreamIsActive(function () {
            return _this._sendStream;
        }, this.__doConnects.bind(this));
    };
    ;
    Conference.prototype.startListenStream = function () {
        var _this = this;
        this.room.on('newpeer', function (peer) {
            console.log('New peer detected:', peer.name);
            _this.__setVideoSource(_this.videoContainer, _this.__startStream(peer));
        });
        if (this.peers[0]) {
            console.log('Existing peer detected:', this.peers[0].name);
            this.__setVideoSource(this.videoContainer, this.__startStream(this.peers[0]));
        }
    };
    ;
    Conference.prototype.stopPublish = function () {
        var _this = this;
        return new Promise(function (resolve, reject) {
            if (_this.ws) {
                _this.room.leave();
                _this.ws.close();
                _this.ws = '';
                if (_this.videoContainer && _this.videoContainer.srcObject) {
                    var stream = _this.videoContainer.srcObject;
                    var tracks = stream instanceof MediaStream ? stream.getTracks() : [];
                    tracks.forEach(function (track) {
                        track.stop();
                    });
                    _this.videoContainer.srcObject = null;
                }
                resolve('Unpublish');
            }
            else {
                reject('Nothing to unpublish');
            }
        });
    };
    ;
    Conference.prototype.__setVideoSource = function (videoContainer, streamFlow) {
        var _this = this;
        this.__whenStreamIsActive(function () {
            return streamFlow || _this._sendStream;
        }, function () {
            if (videoContainer) {
                console.log('Adding active stream');
                videoContainer.style.background = 'black';
                videoContainer.srcObject = streamFlow || _this._sendStream;
                videoContainer.play().then(function (r) { return console.log('Playing'); });
            }
        });
    };
    ;
    Conference.prototype.__whenStreamIsActive = function (getStream, callback) {
        var _this = this;
        var _stream = getStream();
        if (!_stream) {
            return false;
        }
        var id = _stream.id;
        var checkCallback = function () {
            delete _this.streamActiveTimeout[id];
            var stream = getStream();
            if (!stream) {
                return false;
            }
            if (stream.onactive === checkCallback) {
                stream.onactive = null;
            }
            if (!stream.active) {
                // Safari needs a timeout to try again.
                _this.streamActiveTimeout[id] = window.setTimeout(checkCallback, 500);
                return false;
            }
            callback();
        };
        if (_stream.active) {
            callback();
        }
        else if ('onactive' in _stream) {
            _stream.onactive = checkCallback;
        }
        else if (!this.streamActiveTimeout[id]) {
            checkCallback();
        }
        return true;
    };
    ;
    Conference.prototype.__hookup = function (capturing) {
        var vTrack = capturing.stream.getVideoTracks();
        if (capturing.video && vTrack.length > 0) {
            for (var _i = 0, _a = this._sendStream.getVideoTracks(); _i < _a.length; _i++) {
                var track = _a[_i];
                track.stop();
            }
            this._sendStream.addTrack(vTrack[0]);
        }
        var aTrack = capturing.stream.getAudioTracks();
        if (capturing.audio && aTrack.length > 0) {
            for (var _b = 0, _c = this._sendStream.getAudioTracks(); _b < _c.length; _b++) {
                var track = _c[_b];
                track.stop();
            }
            this._sendStream.addTrack(aTrack[0]);
        }
    };
    ;
    Conference.prototype.__doConnects = function () {
        var _this = this;
        if (!this._sendStream) {
            return;
        }
        var aTrack = this._sendStream.getAudioTracks();
        var vTrack = this._sendStream.getVideoTracks();
        var notEnded = function (track) {
            if (track.readyState === 'ended' && _this._sendStream.removeTrack) {
                _this._sendStream.removeTrack(track);
                return false;
            }
            return true;
        };
        this.__connectProducer('audio', aTrack.find(notEnded));
        this.__connectProducer('video', vTrack.find(notEnded));
    };
    ;
    Conference.prototype.__connectProducer = function (type, track) {
        var _type = type === 'audio' ? 'audio' : 'video';
        if (this.producers[_type]) {
            if (this.room && track && this.lastProduced[_type] === track.id) {
                return;
            }
            console.log('Stop producing ', type, this.producers[_type].track.id);
            this.producers[_type].close();
            delete this.producers[_type];
            delete this.lastProduced[_type];
        }
        if (this.room && track) {
            console.log('Producing ', type, track.id);
            this.lastProduced[_type] = track.id;
            var opts = type === 'video' ? { simulcast: true } : {};
            this.producers[_type] = this.room.createProducer(track, opts);
            this.producers[_type].send(this.transport);
        }
    };
    ;
    Conference.prototype.__startStream = function (peer) {
        var _this = this;
        var stream = new MediaStream();
        var profiles = ['low', 'medium', 'high'];
        var profileIndex = 0;
        var showStats = function (s) {
            for (var i = 0; i < s.length; i++) {
                var o = s[i];
                if (o.mediaType === 'video' && o.type === "inbound-rtp") {
                    var kbits = Math.round(o.bitrate / 1000);
                    if (_this.params.minBitrate && _this.params.maxBitrate) {
                        if (!(kbits >= _this.params.minBitrate && kbits <= _this.params.maxBitrate)) {
                            profileIndex++;
                            if (profileIndex < 3) {
                                _this._adjustProfile(profiles[profileIndex]);
                            }
                            else {
                                profileIndex--;
                            }
                        }
                        else {
                        }
                    }
                }
            }
        };
        var addConsumer = function (consumer) {
            if (!consumer.supported) {
                console.log('Consumer', consumer.id, 'not supported');
                return;
            }
            if (consumer.kind === 'video') {
                _this._adjustProfile = _this.__makeAutoAdjustProfile(consumer);
                _this._adjustProfile();
            }
            consumer.on('stats', showStats);
            consumer.enableStats(5000);
            consumer.receive(_this.transport)
                .then(function (track) {
                stream.addTrack(track);
                if (track.kind === 'audio' && !_this.params.audio) {
                    track.enabled = false;
                }
                if (track.kind === 'video' && !_this.params.video) {
                    track.enabled = false;
                }
                consumer.on('close', function () {
                    console.log('Removing the old track', track.id);
                    // clearStats(consumer.kind);
                    stream.removeTrack(track);
                    if (stream.getTracks().length === 0) {
                        console.log('Replacing stream');
                        stream = new MediaStream();
                        _this.__setVideoSource(_this.videoContainer, stream);
                    }
                });
            })
                .catch(function onError(e) {
                console.log('Cannot add track', e);
            });
        };
        peer.on('newconsumer', addConsumer);
        for (var i = 0; i < peer.consumers.length; i++) {
            addConsumer(peer.consumers[i]);
        }
        return stream;
    };
    ;
    Conference.prototype.__makeAutoAdjustProfile = function (videoConsumer) {
        var _this = this;
        videoConsumer.on('effectiveprofilechange', function (profile) {
            _this.params.onProfileChange(profile);
        });
        var declaredProfile;
        return function (desiredProfile) {
            if (!desiredProfile) {
                desiredProfile = 'low';
            }
            var profiles = ['low', 'medium', 'high'];
            if (declaredProfile !== desiredProfile) {
                declaredProfile = desiredProfile;
                videoConsumer.setPreferredProfile(desiredProfile);
            }
            var eprof = videoConsumer.effectiveProfile;
            var eindex = profiles.indexOf(eprof);
            if (eindex < 0) {
                return;
            }
            var pprof = videoConsumer.preferredProfile;
            if (pprof === 'default') {
                pprof = eprof;
                _this.params.onProfileChange(pprof);
            }
            if (pprof !== eprof) {
                return;
            }
            return;
        };
    };
    ;
    Conference.prototype.setPreferredQuality = function (qualityProfile) {
        this._adjustProfile(qualityProfile);
    };
    Conference.prototype.publish = function () {
        var _this = this;
        this.videoContainer = this.params.localVideo instanceof HTMLMediaElement ? this.params.localVideo : document.querySelector(this.params.localVideo);
        return new Promise(function (resolve, reject) {
            try {
                _this.request(_this.getPermissionsUrl('publish'), 'GET').then(function (publishData) {
                    _this.captureDevice().then(function () {
                        _this.setupRoom(publishData)
                            .then(function (ps) {
                            _this.ws = ps.ws;
                            _this.room = ps.room;
                            _this.transport = ps.room.createTransport('send');
                        })
                            .then(function () {
                            _this.startSendStream();
                            if (_this.videoContainer) {
                                var promise = _this.videoContainer.play();
                                if (promise !== undefined) {
                                    promise.then(function (_) {
                                        _this.videoContainer.muted = true;
                                    }).catch(function (error) {
                                        _this.params.errorAutoPlayCallback(error);
                                    });
                                }
                            }
                            resolve('Success');
                        })
                            .catch(function (err) {
                            console.log('Cannot publish to channel: ', err);
                        });
                    }).catch(function (err) {
                        console.log('Cannot publish to channel: ', err);
                    });
                }).catch(function (error) {
                    reject(error);
                });
            }
            catch (e) {
                reject(e);
            }
        });
    };
    Conference.prototype.play = function () {
        var _this = this;
        this.videoContainer = this.params.localVideo instanceof HTMLMediaElement ? this.params.localVideo : document.querySelector(this.params.localVideo);
        return new Promise(function (resolve, reject) {
            try {
                _this.request(_this.getPermissionsUrl('playback'), "GET").then(function (subscribeData) {
                    _this.setupRoom(subscribeData)
                        .then(function (ps) {
                        _this.ws = ps.ws;
                        _this.room = ps.room;
                        _this.peers = ps.peers;
                        _this.transport = ps.room.createTransport('recv');
                    })
                        .then(function () {
                        _this.startListenStream();
                        if (_this.videoContainer) {
                            var promise = _this.videoContainer.play();
                            if (promise !== undefined) {
                                promise.then(function (_) { }).catch(function (error) {
                                    _this.params.errorAutoPlayCallback(error);
                                });
                            }
                        }
                        resolve('Success');
                    })
                        .catch(function (err) {
                        console.log('Cannot subscribe to channel: ', err);
                    });
                }).catch(function (error) {
                    reject(error);
                });
            }
            catch (e) {
                reject(e);
            }
        });
    };
    Conference.prototype.stop = function () {
        var _this = this;
        return new Promise(function (resolve, reject) {
            try {
                _this.stopPublish().then(function (result) {
                    resolve(result);
                }).catch(function (error) {
                    reject(error);
                });
            }
            catch (e) {
                reject(e);
            }
        });
    };
    return Conference;
}());
exports.Conference = Conference;
window.Conference = Conference;


/***/ }),

/***/ 0:
/*!********************!*\
  !*** ws (ignored) ***!
  \********************/
/*! no static exports found */
/***/ (function(module, exports) {

/* (ignored) */

/***/ })

/******/ });
});
//# sourceMappingURL=conference.js.map