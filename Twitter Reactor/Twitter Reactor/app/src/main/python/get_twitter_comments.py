import json
import sys
import os
from tweepy import API
from tweepy import OAuthHandler
from tweepy import Cursor
import socket
import numpy as np
import pickle
from nltk.corpus import stopwords
import string
from os.path import dirname, join
import nltk

def get_twitter_auth():
    try:
        consumer_key = "VhRmg7OjwPbtRlE4MQMSJ9U5O"
        consumer_secret = "HNtelb6dLTQppvrO7PfNP0DJdJzmaRIxXTzsS7eclQpCJsEpeH"
        access_token = "1285812328970190853-8s7YuK0J7aR0qKBiHrRiz5BUVGpYie"
        access_secret = "AeyIhjDPDdTmm3mglxQthI32XmkGxUckfq4zUosA5gJgV"
    except KeyError:
        sys.stderr.write("Error parsing you'r tweepy API keys")
        sys.exit(1)
    auth = OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_token, access_secret)
    return auth

def get_twitter_client():
    auth = get_twitter_auth()
    client = API(auth, wait_on_rate_limit=True)
    return client

def get_comments(query, num_of_comments):
    client = get_twitter_client()
    nltk.download('stopwords')
    # load the model from disk
    model = pickle.load(open(join(dirname(__file__), 'model.pickle'), 'rb'))
    vectorizer = pickle.load(open(join(dirname(__file__), 'tweets_countvectorizer.pickle'), 'rb'))
    dictionary = []
    for comment in Cursor(client.search,  q=query, result_type="recent", tweet_mode="extended").items(num_of_comments):
        if hasattr(comment, 'in_reply_to_status_id_str'):
            comment_vector = vectorizer.transform(np.array([comment.full_text]))
            prediction = model.predict(comment_vector).tolist()[0]
            temp_dict = {"name": comment.author.name, "full comment": comment.full_text, "profile url": comment.author.profile_image_url, "id": comment.id , "prediction": prediction}
            dictionary.append(temp_dict)
    json_string = json.dumps(dictionary, indent = 4)
    return json_string
