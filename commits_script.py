import sys
import pyperclip

if __name__ == "__main__":
	commits = ""
	for i in range(1, len(sys.argv)):
		if len(commits) > 0:
			commits += ", "
		commits += sys.argv[i]
		
	commits = "#commits[" + commits + "]"
	pyperclip.copy(commits)
	print(commits)