import pandas as pd
import re
from os.path import dirname, join

# Functions

def classify(message):

   message = re.sub('\W', ' ', message)
   message = message.lower().split()

   p_spam_given_message = p_spam
   p_ham_given_message = p_ham

   for word in message:
      if word in parameters_spam:
         p_spam_given_message *= parameters_spam[word]

      if word in parameters_ham: 
         p_ham_given_message *= parameters_ham[word]

   print('spam:', p_spam_given_message)
   print('ham:', p_ham_given_message)

   if p_ham_given_message > p_spam_given_message:
      return('The message is legitimate.')
   elif p_ham_given_message < p_spam_given_message:
      return('The message is fraudulent.')
   else:
      return('Equal proabilities, have a human classify this!')


def classify_test_set(message):
 
   message = re.sub('\W', ' ', message)
   message = message.lower().split()

   p_spam_given_message = p_spam
   p_ham_given_message = p_ham

   for word in message:
      if word in parameters_spam:
         p_spam_given_message *= parameters_spam[word]

      if word in parameters_ham:
         p_ham_given_message *= parameters_ham[word]

   if p_ham_given_message > p_spam_given_message:
      return 'ham'
   elif p_spam_given_message > p_ham_given_message:
      return 'spam'
   else:
      return 'needs human classification'

## START

# Read in text file
filename = join(dirname(__file__), "dataset.txt")
with open(filename, encoding='utf-8') as file:
    data = file.readlines()

# Create dataframe
sms_spam = pd.DataFrame(data, columns=['SMS'])
sms_spam['Label'] = sms_spam['SMS'].apply(lambda x: x.split()[0])
sms_spam['SMS'] = sms_spam['SMS'].apply(lambda x: ' '.join(x.split()[1:]))

# Randomize the dataset
data_randomized = sms_spam.sample(frac=1, random_state=1)

# Calculate index for split
training_test_index = round(len(data_randomized) * 0.8)

# Split into training and test sets
training_dataset = data_randomized[:training_test_index].reset_index(drop=True)
test_set = data_randomized[training_test_index:].reset_index(drop=True)

# Before cleaning
training_dataset.head(3)

# After cleaning
training_dataset['SMS'] = training_dataset['SMS'].str.replace('\W', ' ',regex=True) # Removes punctuation
training_dataset['SMS'] = training_dataset['SMS'].str.lower()
training_dataset['SMS'] = training_dataset['SMS'].str.split()

vocabulary = []
for sms in training_dataset['SMS']:
   for word in sms:
      vocabulary.append(word)

vocabulary = list(set(vocabulary))

word_counts_per_sms = {unique_word: [0] * len(training_dataset['SMS']) for unique_word in vocabulary}

for index, sms in enumerate(training_dataset['SMS']):
   for word in sms:
      word_counts_per_sms[word][index] += 1


word_counts = pd.DataFrame(word_counts_per_sms)

cleaned_training_dataset = pd.concat([training_dataset, word_counts], axis=1)


# Isolating spam and ham messages first
spam_messages = cleaned_training_dataset[cleaned_training_dataset['Label'] == 'spam']
ham_messages = cleaned_training_dataset[cleaned_training_dataset['Label'] == 'ham']

# P(Spam) and P(Ham)
p_spam = len(spam_messages) / len(cleaned_training_dataset)
p_ham = len(ham_messages) / len(cleaned_training_dataset)

# N_Spam
n_words_per_spam_message = spam_messages['SMS'].apply(len)
n_spam = n_words_per_spam_message.sum()

# N_Ham
n_words_per_ham_message = ham_messages['SMS'].apply(len)
n_ham = n_words_per_ham_message.sum()

# N_Vocabulary
n_vocabulary = len(vocabulary)

# Laplace smoothing
alpha = 1


# Initiate parameters
parameters_spam = {unique_word:0 for unique_word in vocabulary}
parameters_ham = {unique_word:0 for unique_word in vocabulary}

# Calculate parameters
for word in vocabulary:
   n_word_given_spam = spam_messages[word].sum() # spam_messages already defined
   p_word_given_spam = (n_word_given_spam + alpha) / (n_spam + alpha*n_vocabulary)
   parameters_spam[word] = p_word_given_spam

   n_word_given_ham = ham_messages[word].sum() # ham_messages already defined
   p_word_given_ham = (n_word_given_ham + alpha) / (n_ham + alpha*n_vocabulary)
   parameters_ham[word] = p_word_given_ham

test_set['predicted'] = test_set['SMS'].apply(classify_test_set)
test_set.head()


correct = 0
total = test_set.shape[0]

for row in test_set.iterrows():
   row = row[1]
   if row['Label'] == row['predicted']:
      correct += 1


print('Correct:', correct)
print('Incorrect:', total - correct)
print('Accuracy:', correct/total)

def main(message):
    print(correct/total)
    return classify(message)

