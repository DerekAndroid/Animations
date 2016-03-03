#include "testlibrary.h"

#include <curl/curl.h>
#include <android/log.h>

#ifdef ANDROID
	#include <android/log.h>
	#include <jni.h>
	#ifdef __LP64__
		#define SIZE_T_TYPE "%lu"
	#else
		#define SIZE_T_TYPE "%u"
	#endif	
#endif

#ifdef ANDROID
	#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "TestLibrary", __VA_ARGS__))
#else
	#define LOGI(...) printf(__VA_ARGS__)
#endif

size_t curlCallback(char *data, size_t size, size_t count, void* userdata);

BOOL downloadUrl(const char* url, LPCURL_DOWNLOAD_OBJECT downloadObject ) {
	CURL* curl = curl_easy_init();
	curl_easy_setopt(curl, CURLOPT_URL, url);
	curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, TRUE);
	curl_easy_setopt(curl, CURLOPT_FAILONERROR, TRUE);
	curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, &curlCallback);
	curl_easy_setopt(curl, CURLOPT_WRITEDATA, downloadObject);
	curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, FALSE);
	curl_easy_setopt(curl, CURLOPT_SSL_VERIFYHOST, FALSE);

	CURLcode res = curl_easy_perform(curl);
	if (res != CURLE_OK){
	    LOGI("CURL failed with error code %d", res);
	}
	curl_easy_cleanup(curl);
	return res == CURLE_OK;
}

BOOL postUrl(const char* field, LPCURL_DOWNLOAD_OBJECT postObject ) {
	CURL* curl = curl_easy_init();
	curl_easy_setopt(curl, CURLOPT_NOSIGNAL, 1L);	// Do not install signal handlers
	curl_easy_setopt(curl, CURLOPT_USERAGENT, curl_version());	// set a default user agent
	curl_easy_setopt(curl, CURLOPT_VERBOSE, 1L);   // Display verbose information
	curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, &curlCallback);    // Callback for writing data
	curl_easy_setopt(curl, CURLOPT_WRITEDATA, postObject);	// prevent libcurl from writing the data to stdout
	curl_easy_setopt(curl, CURLOPT_URL, "https://graph.facebook.com/app/app_link_hosts"); // URL to work on
	curl_easy_setopt(curl, CURLOPT_SSL_VERIFYHOST, 0);    // Verify the host name in the SSL certificate
	curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0);    // Verify the SSL certificate
	curl_easy_setopt(curl, CURLOPT_POSTFIELDS, field);

	CURLcode res = curl_easy_perform(curl);
	if (res != CURLE_OK){
		LOGI("CURL failed with error code %d", res);
	}
	curl_easy_cleanup(curl);
	return res == CURLE_OK;
}

size_t curlCallback(char *data, size_t size, size_t count, void* userdata) {
	LOGI("Downloaded data size is " SIZE_T_TYPE, size*count);

    LPCURL_DOWNLOAD_OBJECT downloadObject = (LPCURL_DOWNLOAD_OBJECT) userdata;
    long newSize = 0;
    long offset = 0;
    LPBYTE dataPtr;

    if (downloadObject->data == NULL){
        newSize = size * count * sizeof(BYTE);
        dataPtr = (LPBYTE)malloc(newSize);
    }else{
        newSize = downloadObject->size + (size * count * sizeof(BYTE));
        dataPtr = (LPBYTE)realloc(downloadObject->data, newSize);
        offset = downloadObject->size;
    }

    if (dataPtr==NULL){//malloc or realloc failed
        if (downloadObject->data != NULL){//realloc failed
            free(downloadObject->data);
            downloadObject->data = NULL;
            downloadObject->size = 0;
        }

        return 0; //this will abort the download
    }
    downloadObject->data = dataPtr;
    downloadObject->size = newSize;

    memcpy(downloadObject->data + offset, data, size * count * sizeof(BYTE));
	return size*count;
}

#ifdef ANDROID
extern "C" 
{
	JNIEXPORT jbyteArray JNICALL
	Java_com_example_android_animationsdemo_FacebookSdkActivity_downloadUrl(JNIEnv* env, jobject obj, jstring url ){
		const char* url_c = env->GetStringUTFChars(url, NULL);
		if (!url_c)
			return NULL;

		LOGI( "Download URL: %s", url_c );
		CURL_DOWNLOAD_OBJECT* downloadObject = new CURL_DOWNLOAD_OBJECT;
        downloadObject->data = NULL;
        downloadObject->size=0;

		if (downloadUrl(url_c, downloadObject)){
			env->ReleaseStringUTFChars(url, url_c);
			jbyteArray ret = env->NewByteArray(downloadObject->size);
			env->SetByteArrayRegion(ret, 0, downloadObject->size, (jbyte*)downloadObject->data);
			free(downloadObject->data);
			delete downloadObject;
			return ret;
		}else{
			env->ReleaseStringUTFChars(url, url_c);
			return NULL;
		}
	}

	JNIEXPORT jbyteArray JNICALL
	Java_com_example_android_animationsdemo_FacebookSdkActivity_postUrl(JNIEnv* env, jobject obj, jstring field){
		const char* field_c = env->GetStringUTFChars(field, NULL);
		if (!field_c)
			return NULL;

		LOGI( "Post field: %s", field_c );
		CURL_DOWNLOAD_OBJECT* postObject = new CURL_DOWNLOAD_OBJECT;
        postObject->data = NULL;
        postObject->size=0;

		if (postUrl(field_c, postObject)){
			env->ReleaseStringUTFChars(field, field_c);
			jbyteArray ret = env->NewByteArray(postObject->size);
			env->SetByteArrayRegion(ret, 0, postObject->size, (jbyte*)postObject->data);
			free(postObject->data);
			delete postObject;
			// debug
			int len = env->GetArrayLength(ret);
			unsigned char* buf = new unsigned char[len];
			env->GetByteArrayRegion (ret, 0, len, reinterpret_cast<jbyte*>(buf));
			LOGI( "Result: %s", buf );
			return ret;
		}else{
			env->ReleaseStringUTFChars(field, field_c);
			return NULL;
		}
	}
}
#endif
